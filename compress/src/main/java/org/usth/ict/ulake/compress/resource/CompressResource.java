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
import org.usth.ict.ulake.compress.model.Request;
import org.usth.ict.ulake.compress.model.RequestFile;
import org.usth.ict.ulake.compress.model.Result;
import org.usth.ict.ulake.compress.persistence.RequestFileRepository;
import org.usth.ict.ulake.compress.persistence.RequestRepository;
import org.usth.ict.ulake.compress.persistence.ResultRepository;
import org.usth.ict.ulake.compress.service.CompressTask;
import org.usth.ict.ulake.compress.service.Compressor;
import org.usth.ict.ulake.compress.service.ZipCompressor;


@Path("/compress")
@Produces(MediaType.APPLICATION_JSON)
public class CompressResource {
    private static final Logger log = LoggerFactory.getLogger(CompressResource.class);

    @Inject
    LakeHttpResponse<Request> respReq;

    @Inject
    LakeHttpResponse<Result> respResult;

    @Inject
    LakeHttpResponse<RequestFile> respFiles;

    @Inject
    LakeHttpResponse<Object> respObject;

    @Inject
    RequestRepository repoReq;

    @Inject
    RequestFileRepository repoReqFile;

    @Inject
    ResultRepository repoResp;

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
    @Operation(summary = "List all compression requests. Admin: all possible requests, User: requests of his own.")
    @RolesAllowed({ "User", "Admin" })
    public Response all() {
        Set<String> groups = jwt.getGroups();
        if (groups.contains("Admin")) {
            return respReq.build(200, "", repoReq.listAll());
        }
        Long userId = Long.parseLong(jwt.getClaim(Claims.sub));
        return respReq.build(200, "", repoReq.list("userId", userId));
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Get one request info")
    public Response one(@PathParam("id") @Parameter(description = "Request id to search") Long id) {
        Request req = repoReq.findById(id);
        if (checkOwner(req.userId)) {
            return respReq.build(200, null, req);
        }
        return respReq.build(403);
    }

    @GET
    @Path("/{id}/result")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Check a compression request result")
    public Response status(@PathParam("id") @Parameter(description = "Request id to check status") Long id) {
        Request req = repoReq.findById(id);
        if (!checkOwner(req.userId)) {
            return respReq.build(403);
        }
        Result resp = repoResp.find("requestId=?1 order by id desc", id).firstResult();
        return respResult.build(200, null, resp);
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
        if (entity == null) {
            entity = new Request();
        }
        entity.userId = userId;
        entity.timestamp = new Date().getTime();
        entity.finishedTime = 0L;
        repoReq.persist(entity);
        return respReq.build(200, "", entity);
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
            return respReq.build(404);
        }
        if (!checkOwner(req.userId)) {
            return respReq.build(403);
        }
        entity.requestId = id;
        repoReqFile.persist(entity);
        return respFiles.build(200, "", entity);
    }

    @GET
    @Path("/{id}/files")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "List all files in a compression request")
    public Response getFiles(@PathParam("id") @Parameter(description = "Request id to list") Long id) {
        // check if request is valid
        Request req = repoReq.findById(id);
        if (req == null) {
            return respReq.build(404);
        }
        if (!checkOwner(req.userId)) {
            return respReq.build(403);
        }
        var files = repoReqFile.list("requestId", id);
        return respFiles.build(200, "", files);
    }

    @GET
    @Path("/{id}/count")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Count number of files in a compression request")
    public Response countFiles(@PathParam("id") @Parameter(description = "Request id to count") Long id) {
        // check if request is valid
        Request req = repoReq.findById(id);
        if (req == null) {
            return respReq.build(404);
        }
        if (!checkOwner(req.userId)) {
            return respReq.build(403);
        }
        var count = repoReqFile.count("requestId", id);
        return respObject.build(200, "", count);
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
            return respReq.build(404);
        }
        if (!checkOwner(req.userId)) {
            return respReq.build(403);
        }
        executor.submit(() -> compress(bearer, id));

        return respReq.build(200, "", req);
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
            return respReq.build(403);
        }
        req.finishedTime = -1L;  // indicates that this one is stopped
        repoReq.persist(req);
        return respReq.build(200, "", req);
    }

    @GET
    @Path("/stats")
    @Operation(summary = "Statistics about compression requests")
    @RolesAllowed({ "User", "Admin" })
    public Response tableStats(@HeaderParam("Authorization") String bearer) {
        HashMap<String, Object> ret = new HashMap<>();
        ret.put("count", repoReq.count());
        ret.put("fileCount", repoReqFile.count());
        return respObject.build(200, "", ret);
    }

    /**
     * Start compression service in background with specified id
     * @param id Compression request Id
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void compress(String bearer, Long id) {
        log.info("Start compression in managed executor");
        Compressor compressor = new ZipCompressor(bearer, coreService, fileService);
        CompressTask task = new CompressTask(compressor, id, repoReq, repoReqFile, repoResp);
        task.run();
    }
}
