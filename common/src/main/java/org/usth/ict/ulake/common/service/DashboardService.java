package org.usth.ict.ulake.common.service;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.dashboard.FileFormModel;
import org.usth.ict.ulake.common.model.folder.FileModel;
import org.usth.ict.ulake.common.model.folder.FolderModel;

@Path("/api")
@RegisterRestClient(configKey = "dashboard-api")
@RegisterProvider(value = LakeServiceExceptionMapper.class)
@Produces(MediaType.APPLICATION_JSON)
public interface DashboardService {
    @GET
    @Path("/file")
    @Schema(description = "get list of files")
    public LakeHttpResponse<FileModel> fileList(
        @HeaderParam("Authorization") String bearer);

    @GET
    @Path("/file/{fileId}")
    @Schema(description = "get file information")
    public LakeHttpResponse<FileModel> fileInfo(
        // TODO: HiepLNC refactors ASAP.
        @PathParam("fileId") Long fileId,
        @HeaderParam("Authorization") String bearer);

    @POST
    @Path("/file")
    @Schema(description = "upload new file")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public LakeHttpResponse<FileModel> newFile(
        @HeaderParam("Authorization") String bearer, @MultipartForm FileFormModel file);

    @GET
    @Path("/folder")
    @Schema(description = "list all folder")
    public LakeHttpResponse<FolderModel> folderList(
        @HeaderParam("Authorization") String bearer);

    @GET
    @Path("/folder/{id}")
    @Schema(description = "get folder info")
    public LakeHttpResponse<FolderModel> folderInfo(
        @HeaderParam("Authorization") String bearer,
        @PathParam("id") Long id);

    @GET
    @Path("/folder/root")
    @Schema(description = "list all root folder")
    public LakeHttpResponse<FolderModel> rootInfo(
        @HeaderParam("Authorization") String bearer);

    @POST
    @Path("/folder")
    @Schema(description = "create new folder")
    public LakeHttpResponse<FolderModel> newFolder(
        @HeaderParam("Authorization") String bearer, FolderModel file);

    @DELETE
    @Path("/folder/{id}")
    @Schema(description = "delete folder")
    public LakeHttpResponse<FolderModel> delFolder(
        @HeaderParam("Authorization") String bearer,
        @PathParam("id") Long id);

    @PUT
    @Path("/folder/{id}")
    @Schema(description = "update folder")
    public LakeHttpResponse<FolderModel> updateFolder(
        @HeaderParam("Authorization") String bearer,
        @PathParam("id") Long id,
        @RequestBody FolderModel newData);

    @GET
    @Path("/folder/stats")
    @Schema(description = "get folder statistics")
    public LakeHttpResponse<Object> folderStats(
        @HeaderParam("Authorization") String bearer);
}
