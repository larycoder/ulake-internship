package org.usth.ict.ulake.dashboard.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import org.usth.ict.ulake.dashboard.model.FileModel;
import org.usth.ict.ulake.dashboard.model.extension.ExtensionModel;
import org.usth.ict.ulake.dashboard.model.query.FilterModel;

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
        var filters = new ArrayList<FilterModel>();
        for (String f : filterStr) {
            filters.add(new FilterModel(f));
        }
        String bearer = "bearer " + jwt.getRawToken();
        ExtensionModel<List<FileModel>> files = fileSvc.getListFile(bearer);
        if (files.getCode() == 200) {
            for (var filter : filters) {
                files.setResp(
                    files.getResp()
                    .stream()
                    .filter(o -> filter.filter(o))
                    .collect(Collectors.toList())
                );
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
            var obj = coreSvc.getObjectInfo(file.getResp().getCid(), bearer);
            file.getResp().setObject(obj.getResp());
        } catch (Exception e) {
            ;
        }
        return file;
    }
}
