package org.usth.ict.ulake.ingest.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.ingest.model.CrawlRequest;
import org.usth.ict.ulake.ingest.persistence.FileLogRepo;
import org.usth.ict.ulake.ingest.persistence.CrawlRequestRepo;

@Path("/ingest")
public class CrawlProcessResource {
    @Inject
    LakeHttpResponse<Object> resp;

    @Inject
    CrawlRequestRepo repo;

    @Inject
    FileLogRepo fileLogRepo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "list all request")
    public Response listProcess() {
        return resp.build(200, "", repo.listAll());
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "get crawl request by id")
    public Response one(@PathParam("id") Long id) {
        CrawlRequest process = repo.findById(id);
        if (process == null)
            return resp.build(404, "request not found");
        return resp.build(200, "", process);
    }

    @GET
    @Path("/{id}/files")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "get crawled files of request id")
    public Response files(@PathParam("id") Long id) {
        var listFileLog = fileLogRepo.findByProcessId(id);
        for (var log : listFileLog)
            log.process = null;
        return resp.build(200, "", listFileLog);
    }
}
