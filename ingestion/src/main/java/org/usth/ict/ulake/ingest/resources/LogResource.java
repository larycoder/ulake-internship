package org.usth.ict.ulake.ingest.resources;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.ingest.model.ProcessLog;
import org.usth.ict.ulake.ingest.persistence.FileLogRepo;
import org.usth.ict.ulake.ingest.persistence.ProcessLogRepo;

@Path("/processLog")
public class LogResource {
    @Inject
    LakeHttpResponse resp;

    @Inject
    ProcessLogRepo processLogRepo;

    @Inject
    FileLogRepo fileLogRepo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "list all process")
    public Response listProcess() {
        return resp.build(200, "", processLogRepo.listAll());
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "get crawl process by id")
    public Response one(@PathParam("id") Long id) {
        ProcessLog process = processLogRepo.findById(id);
        if (process == null)
            return resp.build(404, "Process not found");
        return resp.build(200, "", process);
    }

    @GET
    @Path("/{id}/files")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "get crawled files of process id")
    public Response files(@PathParam("id") Long id) {
        return resp.build(200, "", fileLogRepo.findByProcessId(id));
    }
}
