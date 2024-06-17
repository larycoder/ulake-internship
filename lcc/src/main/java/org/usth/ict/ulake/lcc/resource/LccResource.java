package org.usth.ict.ulake.lcc.resource;

import java.util.Date;
import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.quartz.SchedulerException;
import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.lcc.model.Patient;
import org.usth.ict.ulake.lcc.persistence.PatientRepository;
import org.usth.ict.ulake.lcc.service.LccJob;
import org.usth.ict.ulake.lcc.service.LccTask;

import io.quarkus.narayana.jta.QuarkusTransaction;

@Path("/lcc")
@Produces(MediaType.APPLICATION_JSON)
public class LccResource {
    @Inject
    ObjectMapper mapper;

    @Inject
    PatientRepository repo;

    @Inject
    LakeHttpResponse<Object> resp;

    @Inject
    LccTask task;

    @GET
    @RolesAllowed({ "Admin", "User" })
    @Operation(summary = "List all patients")
    public Response all() {
        return resp.build(200, "", repo.listAll());
    }

    @GET
    @Path("/{patientId}/images")
    @RolesAllowed({ "Admin", "User" })
    @Operation(summary = "get patient detected images")
    public Response image(@PathParam("patientId") Long patientId) {
        Patient patient = repo.findById(patientId);
        if (patient == null)
            return resp.build(404, "Could not find out patient");

        if (patient.image == null)
            return resp.build(200, "", null);

        try {
            var type = new TypeReference<List<Object>>() {};
            return resp.build(200, "", mapper.readValue(patient.image, type));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return resp.build(500, "Could not parse image from database");
        }
    }

    @GET
    @Path("/{patientId}/detection")
    @RolesAllowed({ "Admin", "User" })
    @Operation(summary = "Lcc detection for patient image")
    public Response predict(
        @HeaderParam("Authorization") String bearer,
        @PathParam("patientId") Long patientId) {
        Patient patient = repo.findById(patientId);
        if (patient == null)
            return resp.build(404, "Could not find out patient");
        else if (patient.creationTime != null && patient.endTime == null) {
            return resp.build(409, "Task is already run");
        }

        QuarkusTransaction.begin();
        Patient myPatient = repo.findById(patientId);
        myPatient.creationTime = new Date().getTime();
        myPatient.startTime = null;
        myPatient.endTime = null;
        myPatient.status = null;
        myPatient.message = null;
        repo.persist(myPatient);
        QuarkusTransaction.commit();

        try {
            task.start(bearer, patientId, LccJob.class);
            return resp.build(200);
        } catch (SchedulerException e) {
            e.printStackTrace();
            return resp.build(
                       500, "Could not schedule running job for this task");
        }
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
