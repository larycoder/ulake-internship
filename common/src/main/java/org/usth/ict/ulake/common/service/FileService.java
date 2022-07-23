package org.usth.ict.ulake.common.service;

import java.util.List;

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
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.folder.FileModel;
import org.usth.ict.ulake.common.model.folder.FolderModel;
import org.usth.ict.ulake.common.model.folder.UserFileSearchQuery;

@Path("/api")
@RegisterRestClient(configKey = "folder-api")
@RegisterProvider(value = LakeServiceExceptionMapper.class)
@Produces(MediaType.APPLICATION_JSON)
public interface FileService {
    @GET
    @Path("/file")
    @Schema(description = "get list of files")
    public LakeHttpResponse<FileModel> fileList(
        @HeaderParam("Authorization") String bearer);

    @GET
    @Path("/file/{fileId}")
    @Schema(description = "get file information")
    public LakeHttpResponse<FileModel> fileInfo(
        @PathParam("fileId") Long fileId,
        @HeaderParam("Authorization") String bearer);

    @PUT
    @Path("/file/{fileId}")
    @Schema(description = "update file information")
    public LakeHttpResponse<FileModel> fileUpdate(
        @HeaderParam("Authorization") String bearer,
        @PathParam("fileId") Long fileId,
        FileModel file);

    @POST
    @Path("/file")
    @Schema(description = "create new file")
    public LakeHttpResponse<FileModel> newFile(
        @HeaderParam("Authorization") String bearer, FileModel file);

    @GET
    @Path("/file/stats")
    @Schema(description = "get file statistics")
    public LakeHttpResponse fileStats(
        @HeaderParam("Authorization") String bearer);

    @DELETE
    @Path("/file/{fileId}")
    @Schema(description = "Delete a file")
    public LakeHttpResponse<FileModel> deleteFile(
        @HeaderParam("Authorization") String bearer,
        @PathParam("fileId") Long fileId);

    @POST
    @Path("/file/search")
    @Schema(description = "Search list of file")
    public LakeHttpResponse<List<FileModel>> search(
        @HeaderParam("Authorization") String bearer, UserFileSearchQuery fileId);

    @GET
    @Path("/folder")
    @Schema(description = "list all folder")
    public LakeHttpResponse folderList(
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
    public LakeHttpResponse rootInfo(
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
    public LakeHttpResponse folderStats(
        @HeaderParam("Authorization") String bearer);
}
