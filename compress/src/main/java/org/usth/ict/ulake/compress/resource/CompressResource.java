package org.usth.ict.ulake.compress.resource;

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
import org.eclipse.microprofile.context.ThreadContext;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.compress.model.Request;
import org.usth.ict.ulake.compress.model.RequestFile;
import org.usth.ict.ulake.compress.model.Result;
import org.usth.ict.ulake.compress.persistence.RequestFileRepository;
import org.usth.ict.ulake.compress.persistence.RequestRepository;
import org.usth.ict.ulake.compress.persistence.ResultRepository;
import org.usth.ict.ulake.compress.service.CompressCallback;
import org.usth.ict.ulake.compress.service.Compressor;
import org.usth.ict.ulake.compress.service.ZipCompressor;


@Path("/compress")
@Produces(MediaType.APPLICATION_JSON)
public class CompressResource implements CompressCallback {
    private static final Logger log = LoggerFactory.getLogger(CompressResource.class);

    @Inject
    LakeHttpResponse response;

    @Inject
    RequestRepository repoReq;

    @Inject
    RequestFileRepository repoReqFile;

    @Inject
    ResultRepository repoResp;

    @Inject
    JsonWebToken jwt;

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
    @Operation(summary = "List all compression requests. Admin: all possible requests, User: requests of his own.")
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
        Request req = repoReq.findById(id);
        if (checkOwner(req.userId)) {
            return response.build(200, null, req);
        }
        return response.build(403);
    }

    @GET
    @Path("/{id}/status")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Check a compression request status")
    public Response status(@PathParam("id") @Parameter(description = "Request id to check status") Long id) {
        Request req = repoReq.findById(id);
        if (checkOwner(req.userId)) {
            return response.build(200, null, req.finishedTime > 0);
        }
        return response.build(404);
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Create a new compression request")
    public Response post(
        @RequestBody(description = "New compression request to save")
        Request entity) {
        Long userId = Long.parseLong(jwt.getClaim(Claims.sub));
        entity.userId = userId;
        entity.timestamp = new Date().getTime();
        entity.finishedTime = 0L;
        repoReq.persist(entity);
        return response.build(200, "", entity);
    }

    @POST
    @Path("/{id}/file")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Add a new file to a compression request")
    public Response postFile(
        @PathParam("id") @Parameter(description = "Request id to add into") Long id,
        @RequestBody(description = "New file to add into compression request")
        RequestFile entity) {
        // check if request is valid
        Request req = repoReq.findById(id);
        if (req == null) {
            return response.build(404);
        }
        if (!checkOwner(req.userId)) {
            return response.build(403);
        }
        entity.requestId = id;
        repoReqFile.persist(entity);
        return response.build(200, "", entity);
    }

    @POST
    @Path("/{id}/start")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Start a compression request")
    public Response start(
        @HeaderParam("Authorization") String bearer,
        @PathParam("id") @Parameter(description = "Request id to start") Long id) {
        // check if request is valid
        Request req = repoReq.findById(id);
        if (req == null) {
            return response.build(404);
        }
        if (!checkOwner(req.userId)) {
            return response.build(403);
        }
        executor.submit(() -> compress(bearer, id));

        return response.build(200, "", req);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Stop an on-going compression request")
    public Response delete(@PathParam("id") @Parameter(description = "Request id to stop") Long id) {
        Request req = repoReq.findById(id);
        if (!checkOwner(req.userId)) {
            return response.build(403);
        }
        req.finishedTime = -1L;  // indicates that this one is stopped
        repoReq.persist(req);
        return response.build(200, "", req);
    }

    @GET
    @Path("/stats")
    @Operation(summary = "Statistics about compression requests")
    @RolesAllowed({ "User", "Admin" })
    public Response tableStats(@HeaderParam("Authorization") String bearer) {
        HashMap<String, Object> ret = new HashMap<>();
        ret.put("count", repoReq.count());
        ret.put("fileCount", repoReqFile.count());
        return response.build(200, "", ret);
    }

    /**
     * Start compression service in background with specified id
     * @param id Compression request Id
     */
    @Transactional
    private void compress(String bearer, Long id) {
        log.info("Start compression in managed executor");

        Compressor compressor = new ZipCompressor();

        // TODO: split to new task class
        var req = repoReq.findById(id);
        var files = repoReqFile.list("requestId", id);
        var result = new Result();
        result.requestId = id;
        result.ownerId = req.userId;
        result.totalFiles = (long) files.size();
        repoResp.persist(result);

        // go
        compressor.compress(files, result, this);

        // mark as finished in the request object
        req.finishedTime = new Date().getTime();
        repoReq.persist(req);
    }

    @Override
    public void callback(RequestFile file, Result result) {
        result.totalFiles++;
        log.info("  + Compression task callback file {}", file.fileId);
        repoResp.persist(result);
    }
}
