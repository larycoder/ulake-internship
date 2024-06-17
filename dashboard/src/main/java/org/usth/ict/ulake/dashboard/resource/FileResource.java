package org.usth.ict.ulake.dashboard.resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.core.ObjectFormModel;
import org.usth.ict.ulake.common.model.core.ObjectModel;
import org.usth.ict.ulake.common.model.dashboard.FileFormModel;
import org.usth.ict.ulake.common.model.folder.FileModel;
import org.usth.ict.ulake.common.service.CoreService;
import org.usth.ict.ulake.common.service.FileService;
import org.usth.ict.ulake.common.service.exception.LakeServiceForbiddenException;
import org.usth.ict.ulake.common.service.exception.LakeServiceNotFoundException;
import org.usth.ict.ulake.dashboard.filter.FilterModel;
import org.usth.ict.ulake.dashboard.filter.QueryException;
import org.usth.ict.ulake.dashboard.filter.impl.FilterServiceImpl;
import org.usth.ict.ulake.dashboard.model.ObjectMeta;
import org.usth.ict.ulake.dashboard.service.IrFeatureExtractJob;
import org.usth.ict.ulake.dashboard.service.IrFeatureExtractTask;

@Path("/file")
@Tag(name = "File")
public class FileResource {
    private static final Logger log = LoggerFactory.getLogger(FileResource.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    @RestClient
    FileService fileSvc;

    @Inject
    @RestClient
    CoreService coreSvc;

    @Inject
    IrFeatureExtractTask irTask;

    @Inject
    LakeHttpResponse<Object> resp;

    @Inject
    JsonWebToken jwt;

    @GET
    @RolesAllowed({"User", "Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "list all file of user")
    public Response file(
        @QueryParam("filter") List<String> filterStr) {
        String bearer = "bearer " + jwt.getRawToken();

        // collect filters
        var filters = new ArrayList<FilterModel>();
        for (String f : filterStr) {
            filters.add(new FilterModel(f));
        }

        // filter data
        var fileResp = fileSvc.fileList(bearer);
        var type = new TypeReference<List<FileModel>>() {};
        var files = mapper.convertValue(fileResp.getResp(), type);
        var filterSvc = new FilterServiceImpl<FileModel>();

        try {
            for (FilterModel filter : filters) {
                files = filterSvc.filter(files, filter);
            }
        } catch (QueryException e) {
            resp.build(400, "Query exception", e.getMessage());
        }

        return resp.build(200, null, files);
    }

    @GET
    @Path("/{fileId}")
    @RolesAllowed({"User", "Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "get file detail")
    public Response fileInfo(
        @PathParam("fileId") Long fileId) {
        String bearer = "bearer " + jwt.getRawToken();
        var file = fileSvc.fileInfo(fileId, bearer).getResp();
        return resp.build(200, null, file);
    }

    @POST
    @RolesAllowed({"User", "Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(summary = "upload new file")
    public Response post(
            @RequestBody(description = "multipart information for upload file to lake")
            @MultipartForm FileFormModel input) {
        String bearer = "bearer " + jwt.getRawToken();

        FileModel fileInfo = input.fileInfo;
        fileInfo.ownerId = Long.parseLong(jwt.getClaim(Claims.sub));
        var output = new ObjectFormModel();
        output.is = input.is;

        try {
            var objMeta = new ObjectMeta();
            objMeta.name = fileInfo.name;
            objMeta.length = fileInfo.size;
            output.metadata = mapper.writeValueAsString(objMeta);
        } catch (JsonProcessingException e) {
            output.metadata = null;
            log.info("Could not parse metadata to string ", e);
        }

        if (output.is == null || output.metadata == null || fileInfo == null) {
            return resp.build(403, "Invalid arguments");
        }

        ObjectModel obj;
        try {
            var objJson = coreSvc.newObject(bearer, output).getResp();
            obj = mapper.convertValue(objJson, ObjectModel.class);
        } catch (Exception e) {
            log.error("Fail to create new object", e);
            return resp.build(500, "Fail to create new object", e.getMessage());
        }

        FileModel fileResp;
        try {
            fileInfo.cid = obj.cid;
            fileInfo.creationTime = new Date().getTime();
            fileResp = fileSvc.newFile(bearer, fileInfo).getResp();
        } catch (Exception e) {
            log.error("Fail to create new file from object", e);
            return resp.build(500, "Fail to create new file from object");
        }

        // check mime and perform feature extraction
        if (fileResp.mime != null && fileResp.mime.startsWith("image/")) {
            try {
                irTask.start(bearer, fileResp.id, IrFeatureExtractJob.class);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
        return resp.build(200, null, fileResp);
    }

    @PUT
    @Path("/{fileId}")
    @RolesAllowed({"User", "Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "update file detail")
    public Response fileUpdate(
        @HeaderParam("Authorization") String bearer,
        @PathParam("fileId") Long fileId,
        FileModel file) {
        try {
            var resp = fileSvc.fileUpdate(bearer, fileId, file);
            return resp.build(200, null, file);
        }
        catch (LakeServiceNotFoundException e) {
            return resp.build(404);
        }
        catch (LakeServiceForbiddenException e) {
            return resp.build(403);
        }
    }

    @DELETE
    @Path("/{fileId}")
    @RolesAllowed({"User", "Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete a file")
    public Response delete(
        @HeaderParam("Authorization") String bearer,
        @PathParam("fileId") Long fileId) {
        try {
            var file = fileSvc.deleteFile(bearer, fileId).getResp();
            return resp.build(200, null, file);
        } catch (LakeServiceNotFoundException e) {
            return resp.build(404);
        }
    }
}
