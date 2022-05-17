package org.usth.ict.ulake.common.service;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.folder.FileModel;

@Path("/api")
@RegisterRestClient(configKey = "folder-api")
@Produces(MediaType.APPLICATION_JSON)
public interface FileService {
    @GET
    @Path("/file")
    @Schema(description = "get list of files")
    public LakeHttpResponse fileList(
        @HeaderParam("Authorization") String bearer);

    @GET
    @Path("/file/{fileId}")
    @Schema(description = "get file information")
    public LakeHttpResponse fileInfo(
        @PathParam("fileId") Long fileId,
        @HeaderParam("Authorization") String bearer);

    @POST
    @Path("/file")
    @Schema(description = "create new file")
    public LakeHttpResponse newFile(
        @HeaderParam("Authorization") String bearer, FileModel file);
}
