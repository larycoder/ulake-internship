package org.usth.ict.ulake.common.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.dashboard.FileFormModel;
import org.usth.ict.ulake.common.model.folder.FolderModel;

@Path("/api")
@RegisterRestClient(configKey = "dashboard-api")
@RegisterProvider(value = LakeServiceExceptionMapper.class)
@Produces(MediaType.APPLICATION_JSON)
public interface DashboardService {
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
    @Schema(description = "upload new file")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public LakeHttpResponse newFile(
        @HeaderParam("Authorization") String bearer, @MultipartForm FileFormModel file);

    @GET
    @Path("/folder")
    @Schema(description = "list all folder")
    public LakeHttpResponse folderList(
        @HeaderParam("Authorization") String bearer);

    @GET
    @Path("/folder/{id}")
    @Schema(description = "get folder info")
    public LakeHttpResponse folderInfo(
        @HeaderParam("Authorization") String bearer,
        @PathParam("id") Long id);

    @GET
    @Path("/folder/root")
    @Schema(description = "list all root folder")
    public LakeHttpResponse rootInfo(
        @HeaderParam("Authorization") String bearer);

    @POST
    @Path("/folder")
    @Schema(description = "create new folder")
    public LakeHttpResponse newFolder(
        @HeaderParam("Authorization") String bearer, FolderModel file);

    @DELETE
    @Path("/folder/{id}")
    @Schema(description = "delete folder")
    public LakeHttpResponse delFolder(
        @HeaderParam("Authorization") String bearer,
        @PathParam("id") Long id);

    @PUT
    @Path("/folder/{id}")
    @Schema(description = "update folder")
    public LakeHttpResponse updateFolder(
        @HeaderParam("Authorization") String bearer,
        @PathParam("id") Long id,
        @RequestBody FolderModel newData);

    @GET
    @Path("/folder/stats")
    @Schema(description = "get folder statistics")
    public LakeHttpResponse folderStats(
        @HeaderParam("Authorization") String bearer);
}
