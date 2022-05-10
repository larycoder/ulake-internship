package org.usth.ict.ulake.dashboard.extension;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.usth.ict.ulake.dashboard.model.FileModel;
import org.usth.ict.ulake.dashboard.model.extension.ExtensionModel;

@Path("/api")
@RegisterRestClient(configKey = "folder-api")
@Produces(MediaType.APPLICATION_JSON)
public interface FileService {
    @GET
    @Path("/file")
    public ExtensionModel<List<FileModel>> getListFile(
        @HeaderParam("Authorization") String bearer);

    @GET
    @Path("/file/{fileId}")
    public ExtensionModel<FileModel> getFileInfo(
        @PathParam("fileId") Long fileId,
        @HeaderParam("Authorization") String bearer);
}
