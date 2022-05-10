package org.usth.ict.ulake.dashboard.resource;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.usth.ict.ulake.dashboard.extension.CoreService;
import org.usth.ict.ulake.dashboard.extension.FileService;
import org.usth.ict.ulake.dashboard.filter.FilterModel;
import org.usth.ict.ulake.dashboard.filter.QueryException;
import org.usth.ict.ulake.dashboard.filter.impl.FilterServiceImpl;
import org.usth.ict.ulake.dashboard.model.FileModel;
import org.usth.ict.ulake.dashboard.model.extension.ExtensionModel;

@Path("/file")
@Tag(name = "File")
public class FileResource {

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
    public ExtensionModel<FileModel> fileInfo(
        @PathParam("fileId") Long fileId) {
        String bearer = "bearer " + jwt.getRawToken();
        var file = fileSvc.getFileInfo(fileId, bearer);
        try {
            var obj = coreSvc.getObjectInfo(file.getResp().cid, bearer);
            file.getResp().object = obj.getResp();
        } catch (Exception e) {
            ;
        }
        return file;
    }
}
