package org.usth.ict.ulake.lcc.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.folder.FileModel;
import org.usth.ict.ulake.common.service.CoreService;
import org.usth.ict.ulake.common.service.DashboardService;
import org.usth.ict.ulake.common.service.exception.LakeServiceForbiddenException;
import org.usth.ict.ulake.common.service.exception.LakeServiceNotFoundException;
import org.usth.ict.ulake.lcc.model.Patient;
import org.usth.ict.ulake.lcc.persistence.PatientRepository;
import org.usth.ict.ulake.lcc.service.FlaskAppService;

@Path("/lcc")
@Produces(MediaType.APPLICATION_JSON)
public class LccResource {

    @Inject
    PatientRepository repo;

    @Inject
    @RestClient
    CoreService core;

    @Inject
    @RestClient
    DashboardService dashboard;

    @Inject
    @RestClient
    FlaskAppService flaskApp;

    @Inject
    LakeHttpResponse<Object> resp;

    @ConfigProperty(name = "ulake.lcc.data")
    String dataPath;

    @GET
    @RolesAllowed({ "Admin", "User" })
    @Operation(summary = "List all patients")
    public Response all() {
        return resp.build(200, "", repo.listAll());
    }

    @GET
    @Path("/{patientId}/prediction")
    @RolesAllowed({ "Admin", "User" })
    @Operation(summary = "Predict patient image")
    public Response predict(
        @HeaderParam("Authorization") String bearer,
        @PathParam("patientId") Long patientId) {
        Patient patient = repo.findById(patientId);
        if (patient == null)
            return resp.build(404, "Patient not found");

        FileModel fileInfo;
        try {
            fileInfo = dashboard.fileInfo(patient.fileId, bearer).getResp();
        } catch (LakeServiceNotFoundException e) {
            e.printStackTrace();
            return resp.build(404, "Patient file not found");
        } catch (LakeServiceForbiddenException e) {
            e.printStackTrace();
            return resp.build(403, "Patient file not found");
        }

        var filePath = Paths.get(dataPath, fileInfo.cid);
        if (!Files.exists(filePath)) {
            try {
                File newFile = new File(filePath.toString());
                newFile.createNewFile();
                core.objectDataByFileId(patient.fileId, bearer).transferTo(
                    new FileOutputStream(new File(filePath.toString())));
            } catch (IOException e) {
                e.printStackTrace();
                return resp.build(500, "Fail to load file to local");
            }
        }

        var flaskResp = flaskApp.predict(fileInfo.cid);
        return resp.build(
                   flaskResp.getCode(), flaskResp.getMsg(), flaskResp.getResp());
    }

    @POST
    @RolesAllowed({ "Admin" })
    @Transactional
    @Operation(summary = "Add new patient info")
    public Response post(Patient entry) {
        repo.persist(entry);
        return resp.build(200, "", entry);
    }

    @PUT
    @Path("/{patientId}")
    @RolesAllowed({ "Admin" })
    @Transactional
    @Operation(summary = "Update patient info by patientId")
    public Response update(
        @PathParam("patientId") Long patientId, Patient entry) {
        Patient patient = repo.findById(patientId);
        if (patient == null)
            return resp.build(404, "Patient not found");

        if (entry.fileId != null && entry.fileId > 0)
            patient.fileId = entry.fileId;
        if (!Utils.isEmpty(entry.name))
            patient.name = entry.name;
        if (!Utils.isEmpty(entry.modality))
            patient.modality = entry.modality;
        if (!Utils.isEmpty(entry.gender))
            patient.gender = entry.gender;
        if (entry.studyDate != null)
            patient.studyDate = entry.studyDate;

        repo.persist(patient);
        return resp.build(200, "", patient);
    }

    @DELETE
    @Path("/{patientId}")
    @RolesAllowed({ "Admin" })
    @Transactional
    @Operation(summary = "Delete patient info")
    public Response del(@PathParam("patientId") Long patientId) {
        return resp.build(200, "", repo.deleteById(patientId));
    }
}
