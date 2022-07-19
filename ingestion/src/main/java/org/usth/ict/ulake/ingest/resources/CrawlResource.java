package org.usth.ict.ulake.ingest.resources;

import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.usth.ict.ulake.common.service.DashboardService;
import org.usth.ict.ulake.ingest.model.Policy;
import org.usth.ict.ulake.ingest.model.macro.FetchConfig;
import org.usth.ict.ulake.ingest.persistence.FileLogRepo;
import org.usth.ict.ulake.ingest.persistence.ProcessLogRepo;
import org.usth.ict.ulake.ingest.services.CrawlSvc;

@Path("/crawl")
public class CrawlResource {
    @Inject
    ObjectMapper mapper;

    @Inject
    @RestClient
    DashboardService dashboardSvc;

    @Inject
    ProcessLogRepo processRepo;

    @Inject
    FileLogRepo fileRepo;

    @Inject
    JsonWebToken jwt;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "call crawl process")
    @Transactional
    public Map<String, Object> crawl(
        @HeaderParam("Authorization") String token,
        @Parameter(description = "(FETCH to dry run, DOWNLOAD to crawl data")
        @QueryParam("mode") FetchConfig mode,
        @Parameter(description = "folder to store crawled files")
        @QueryParam("folderId") Long folderId,
        @Parameter(description = "brief-description of crawl process")
        @QueryParam("desc") String desc,
        @RequestBody(description = "instruction of crawl") Policy policy) {

        var svc = new CrawlSvc(dashboardSvc, jwt, processRepo, fileRepo);
        return svc.runCrawl(policy, mode, folderId, desc);
    }
}
