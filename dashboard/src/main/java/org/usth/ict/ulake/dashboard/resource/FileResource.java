package org.usth.ict.ulake.dashboard.resource;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.dashboard.extension.CoreService;
import org.usth.ict.ulake.dashboard.extension.FileService;
import org.usth.ict.ulake.dashboard.filter.FilterModel;
import org.usth.ict.ulake.dashboard.filter.QueryException;
import org.usth.ict.ulake.dashboard.filter.impl.FilterServiceImpl;
import org.usth.ict.ulake.dashboard.model.FileModel;
import org.usth.ict.ulake.dashboard.model.FileObjectFormModel;
import org.usth.ict.ulake.dashboard.model.ObjectFormModel;
import org.usth.ict.ulake.dashboard.model.ObjectMeta;
import org.usth.ict.ulake.dashboard.model.ObjectModel;

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
    LakeHttpResponse resp;

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
            resp.build(400, e.toString());
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
        @MultipartForm FileObjectFormModel input) {
        String bearer = "bearer " + jwt.getRawToken();

        FileModel fileInfo = input.fileInfo;
        var output = new ObjectFormModel();
        output.is = input.is;

        try {
            var objMeta = new ObjectMeta();
            objMeta.name = fileInfo.name;
            objMeta.length = fileInfo.size;
            output.metadata = mapper.writeValueAsString(objMeta);
        } catch (JsonProcessingException e) {
            output.metadata = null;
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
            return resp.build(500, e.toString());
        }

        try {
            fileInfo.cid = obj.cid;
            var fileResp = fileSvc.newFile(bearer, fileInfo);
            return resp.build(200, null, fileResp.getResp());
        } catch (Exception e) {
            log.error("Fail to create new file from object", e);
            return resp.build(500, "Fail to create new file from object");
        }
    }
}
