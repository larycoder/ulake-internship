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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.usth.ict.ulake.dashboard.model.extension.ExtensionModel;

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
    JsonWebToken jwt;

    @GET
    @RolesAllowed({"User", "Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "list all file of user")
    public ExtensionModel<List<FileModel>> file(
        @QueryParam("filter") List<String> filterStr) {
        // collect filters
        var filters = new ArrayList<FilterModel>();
        for (String f : filterStr) {
            filters.add(new FilterModel(f));
        }

        // filter data
        String bearer = "bearer " + jwt.getRawToken();
        ExtensionModel<List<FileModel>> files = fileSvc.getListFile(bearer);
        var filterSvc = new FilterServiceImpl<FileModel>();
        if (files.getCode() == 200) {
            try {
                for (FilterModel filter : filters) {
                    files.setResp(filterSvc.filter(files.getResp(), filter));
                }
            } catch (QueryException e) {
                files.setCode(400);
                files.setMsg(e.toString());
                files.setResp(null);
            }
        }
        return files;
    }

    @GET
    @Path("/{fileId}")
    @RolesAllowed({"User", "Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "get file detail")
    public ExtensionModel<FileModel> fileInfo(
        @PathParam("fileId") Long fileId) {
        String bearer = "bearer " + jwt.getRawToken();
        var file = fileSvc.getFileInfo(fileId, bearer);
        return file;
    }

    @POST
    @RolesAllowed({"User", "Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(summary = "upload new file")
    public ExtensionModel<?> post(
        @RequestBody(description = "multipart information for upload file to lake")
        @MultipartForm FileObjectFormModel input) {
        String bearer = "bearer " + jwt.getRawToken();
        var resp = new ExtensionModel<String>();

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
            resp.setCode(403);
            resp.setMsg("Invalid input");
            return resp;
        }

        ObjectModel obj;
        try {
            obj = coreSvc.newObject(bearer, output).getResp();
        } catch (Exception e) {
            log.error("Fail to create new object", e);
            resp.setCode(500);
            resp.setMsg("Fail to create new object");
            resp.setResp(e.toString());
            return resp;
        }

        try {
            fileInfo.cid = obj.cid;
            return fileSvc.newFile(bearer, fileInfo);
        } catch (Exception e) {
            log.error("Fail to create new file from object", e);
            resp.setCode(500);
            resp.setMsg("Fail to create new file from object");
            resp.setResp(e.toString());
            return resp;
        }
    }
}
