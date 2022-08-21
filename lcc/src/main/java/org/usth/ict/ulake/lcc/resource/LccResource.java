package org.usth.ict.ulake.lcc.resource;

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

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.lcc.model.Patient;
import org.usth.ict.ulake.lcc.persistence.PatientRepository;

@Path("/lcc")
@Produces(MediaType.APPLICATION_JSON)
public class LccResource {

    @Inject
    PatientRepository repo;

    @Inject
    LakeHttpResponse<Object> resp;

    @GET
    @RolesAllowed({ "Admin", "User" })
    @Operation(summary = "List all patients")
    public Response all(@HeaderParam("Authorization") String bearer) {
        return resp.build(200, "", repo.listAll());
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
