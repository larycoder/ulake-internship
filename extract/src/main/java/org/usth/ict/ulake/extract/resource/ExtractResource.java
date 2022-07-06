package org.usth.ict.ulake.extract.resource;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.service.CoreService;
import org.usth.ict.ulake.common.service.FileService;
import org.usth.ict.ulake.extract.model.ExtractRequest;
import org.usth.ict.ulake.extract.model.ExtractResult;
import org.usth.ict.ulake.extract.persistence.ExtractRequestRepository;
import org.usth.ict.ulake.extract.persistence.ExtractResultFileRepository;
import org.usth.ict.ulake.extract.persistence.ExtractResultRepository;
import org.usth.ict.ulake.extract.service.ExtractTask;
import org.usth.ict.ulake.extract.service.Extractor;
import org.usth.ict.ulake.extract.service.ZipExtractor;


@Path("/extract")
@Produces(MediaType.APPLICATION_JSON)
public class ExtractResource {
    private static final Logger log = LoggerFactory.getLogger(ExtractResource.class);

    @Inject
    LakeHttpResponse response;

    @Inject
    ExtractRequestRepository repoReq;

    @Inject
    ExtractResultFileRepository repoResFile;

    @Inject
    ExtractResultRepository repoRes;

    @Inject
    JsonWebToken jwt;

    @Inject
    @RestClient
    CoreService coreService;

    @Inject
    @RestClient
    FileService fileService;

    @Inject
    ManagedExecutor executor;

    /**
     * Check if an userId is valid for the current request
     */
    private boolean checkOwner(Long userId) {
        Long jwtUserId = Long.parseLong(jwt.getClaim(Claims.sub));
        Set<String> groups = jwt.getGroups();
        return (groups.contains("Admin") || userId == jwtUserId);
    }

    @GET
    @Operation(summary = "List all extraction requests. Admin: all possible requests, User: requests of his own.")
    @RolesAllowed({ "User", "Admin" })
    public Response all() {
        Set<String> groups = jwt.getGroups();
        if (groups.contains("Admin")) {
            return response.build(200, "", repoReq.listAll());
        }
        Long userId = Long.parseLong(jwt.getClaim(Claims.sub));
        return response.build(200, "", repoReq.list("userId", userId));
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Get one request info")
    public Response one(@PathParam("id") @Parameter(description = "Request id to search") Long id) {
        ExtractRequest req = repoReq.findById(id);
        if (checkOwner(req.userId)) {
            return response.build(200, null, req);
        }
        return response.build(403);
    }

    @GET
    @Path("/{id}/result")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Check an extraction request result")
    public Response status(@PathParam("id") @Parameter(description = "Request id to check status") Long id) {
        ExtractRequest req = repoReq.findById(id);
        if (!checkOwner(req.userId)) {
            return response.build(403);
        }
        ExtractResult resp = repoRes.find("requestId=?1 order by id desc", id).firstResult();
        return response.build(200, null, resp);
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Create a new extraction request")
    public Response post(
        @RequestBody(description = "New extraction request to save")
        ExtractRequest entity) {
        Long userId = Long.parseLong(jwt.getClaim(Claims.sub));
        entity.id = null;   // auto generated.
        entity.userId = userId;
        entity.timestamp = new Date().getTime();
        entity.finishedTime = 0L;
        repoReq.persist(entity);
        return response.build(200, "", entity);
    }

    @GET
    @Path("/{id}/files")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "List all files in an extraction request")
    public Response getFiles(@PathParam("id") @Parameter(description = "Request id to list") Long id) {
        // check if request is valid
        ExtractRequest req = repoReq.findById(id);
        if (req == null) {
            return response.build(404);
        }
        if (!checkOwner(req.userId)) {
            return response.build(403);
        }
        var files = repoResFile.list("requestId", id);
        return response.build(200, "", files);
    }

    @GET
    @Path("/{id}/count")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Count number of files in an extraction request")
    public Response countFiles(@PathParam("id") @Parameter(description = "Request id to count") Long id) {
        // check if request is valid
        ExtractRequest req = repoReq.findById(id);
        if (req == null) {
            return response.build(404);
        }
        if (!checkOwner(req.userId)) {
            return response.build(403);
        }
        var count = repoResFile.count("requestId", id);
        return response.build(200, "", count);
    }

    @POST
    @Path("/{id}/start")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Start an extraction request")
    public Response start(
        @HeaderParam("Authorization") String bearer,
        @PathParam("id") @Parameter(description = "Request id to start") Long id) {
        // check if request is valid
        ExtractRequest req = repoReq.findById(id);
        if (req == null) {
            return response.build(404);
        }
        if (!checkOwner(req.userId)) {
            return response.build(403);
        }
        executor.submit(() -> extract(bearer, id));

        return response.build(200, "", req);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Stop an on-going extraction request")
    public Response delete(@PathParam("id") @Parameter(description = "Request id to stop") Long id) {
        ExtractRequest req = repoReq.findById(id);
        if (!checkOwner(req.userId)) {
            return response.build(403);
        }
        req.finishedTime = -1L;  // indicates that this one is stopped
        repoReq.persist(req);
        return response.build(200, "", req);
    }

    @GET
    @Path("/stats")
    @Operation(summary = "Statistics about extraction requests")
    @RolesAllowed({ "User", "Admin" })
    public Response tableStats(@HeaderParam("Authorization") String bearer) {
        HashMap<String, Object> ret = new HashMap<>();
        ret.put("count", repoReq.count());
        ret.put("fileCount", repoResFile.count());
        return response.build(200, "", ret);
    }

    /**
     * Start extraction service in background with specified id
     * @param id Compression request Id
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void extract(String bearer, Long id) {
        log.info("Start extraction in managed executor");
        Extractor compressor = new ZipExtractor(bearer, coreService, fileService);
        ExtractTask task = new ExtractTask(compressor, id, repoReq, repoResFile, repoRes);
        task.run();
    }
}
