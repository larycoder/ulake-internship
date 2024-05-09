package org.usth.ict.ulake.lcc.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.folder.FileModel;
import org.usth.ict.ulake.common.service.CoreService;
import org.usth.ict.ulake.common.service.DashboardService;
import org.usth.ict.ulake.common.service.exception.LakeServiceForbiddenException;
import org.usth.ict.ulake.common.service.exception.LakeServiceNotFoundException;
import org.usth.ict.ulake.common.task.ScheduledTask;
import org.usth.ict.ulake.lcc.model.Patient;
import org.usth.ict.ulake.lcc.persistence.PatientRepository;

import io.quarkus.narayana.jta.QuarkusTransaction;

@ApplicationScoped
public class LccTask extends ScheduledTask {
    private static final Logger log = LoggerFactory.getLogger(LccTask.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    PatientRepository repo;

    @Inject
    LakeHttpResponse<Object> resp;

    @Inject
    @RestClient
    CoreService core;

    @Inject
    @RestClient
    DashboardService dashboard;

    @Inject
    @RestClient
    FlaskAppService flaskApp;

    @ConfigProperty(name = "ulake.lcc.data")
    String dataPath;

    @ActivateRequestContext
    private void commitTask(
        Long patientId, Integer status, String msg, String resp) {
        QuarkusTransaction.begin();
        Patient patient = repo.findById(patientId);
        patient.endTime = new Date().getTime();
        patient.status = status;
        patient.message = msg;
        patient.image = resp;
        repo.persist(patient);
        QuarkusTransaction.commit();
    }

    private void commitTask(Long patientId, Integer status, String msg) {
        commitTask(patientId, status, msg, null);
    }

    @ActivateRequestContext
    public void run(String bearer, Long patientId) {
        // start lcc process
        QuarkusTransaction.begin();
        Patient patient = repo.findById(patientId);
        patient.startTime = new Date().getTime();
        repo.persist(patient);
        QuarkusTransaction.commit();

        FileModel fileInfo;
        try {
            fileInfo = dashboard.fileInfo(patient.fileId, bearer).getResp();
        } catch (LakeServiceNotFoundException e) {
            e.printStackTrace();
            commitTask(patientId, 404, "Patient file not found");
            return;
        } catch (LakeServiceForbiddenException e) {
            e.printStackTrace();
            commitTask(patientId, 403, "Patient file forbidden");
            return;
        }

        var filePath = Paths.get(dataPath, fileInfo.cid);
        log.info("Check file in path: " + filePath);
        if (!Files.exists(filePath)) {
            try {
                File newFile = new File(filePath.toString());
                newFile.createNewFile();
                core.objectDataByFileId(patient.fileId, bearer).transferTo(
                    new FileOutputStream(new File(filePath.toString())));
            } catch (IOException e) {
                e.printStackTrace();
                commitTask(patientId, 500, "Fail to load file to local");
                return;
            }
        }

        try {
            var flaskResp = flaskApp.predict(fileInfo.cid);
            String image = mapper.writeValueAsString(flaskResp.getResp());
            commitTask(
                patientId, flaskResp.getCode(), flaskResp.getMsg(), image);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            commitTask(patientId, 500, "Fail during running detection process");
            return;
        }
    }
}
