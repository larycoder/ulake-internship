package org.usth.ict.ulake.common.service;

import java.io.InputStream;

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
import org.usth.ict.ulake.common.model.core.GroupObjectModel;
import org.usth.ict.ulake.common.model.core.ObjectFormModel;
import org.usth.ict.ulake.common.model.core.ObjectModel;

@Path("/api")
@RegisterRestClient(configKey = "core-api")
@RegisterProvider(value = LakeServiceExceptionMapper.class)
@Produces(MediaType.APPLICATION_JSON)
public interface CoreService {
    @GET
    @Path("/object")
    @Schema(description = "list all object of user")
    public LakeHttpResponse<ObjectModel> objectList(
        @HeaderParam("Authorization") String bearer);

    @GET
    @Path("/object/{cid}")
    @Schema(description = "get object information")
    public LakeHttpResponse<ObjectModel> objectInfo(
        @PathParam("cid") String cid,
        @HeaderParam("Authorization") String bearer);

    @GET
    @Path("/object/{cid}/data")
    @Schema(description = "load binary object from core")
    public InputStream objectData(
        @PathParam("cid") String cid,
        @HeaderParam("Authorization") String bearer);

    @GET
    @Path("/object/{fileId}/fileData")
    @Schema(description = "load binary object from core by file id")
    public InputStream objectDataByFileId(
        @PathParam("fileId") Long fileId,
        @HeaderParam("Authorization") String bearer);

    @POST
    @Path("/object")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Schema(description = "upload new object")
    public LakeHttpResponse<ObjectModel> newObject(
        @HeaderParam("Authorization") String bearer,
        @MultipartForm ObjectFormModel output);

    @GET
    @Path("/group/{id}")
    @Schema(description = "get group info")
    public LakeHttpResponse<GroupObjectModel> groupInfo(
        @PathParam("id") Long id,
        @HeaderParam("Authorization") String bearer);

    @POST
    @Path("/group")
    @Schema(description = "create new group")
    public LakeHttpResponse<GroupObjectModel> newGroup(
        @RequestBody GroupObjectModel entity,
        @HeaderParam("Authorization") String bearer);

    @PUT
    @Path("/group/{id}")
    @Schema(description = "update group")
    public LakeHttpResponse<GroupObjectModel> updateGroup(
        @PathParam("id") Long id,
        @RequestBody GroupObjectModel newEntity,
        @HeaderParam("Authorization") String bearer);

    @DELETE
    @Path("/group/{id}")
    @Schema(description = "delete group")
    public LakeHttpResponse<GroupObjectModel> delGroup(
        @PathParam("id") Long id,
        @HeaderParam("Authorization") String bearer);

    @GET
    @Path("/object/stats")
    @Schema(description = "Get all statistics from core service")
    public LakeHttpResponse<Object> stats(
        @HeaderParam("Authorization") String bearer);

    @GET
    @Path("/temp/{cid}/data")
    @Schema(description = "load temporary binary object from core")
    public InputStream tempData(
        @PathParam("cid") String cid,
        @HeaderParam("Authorization") String bearer);

    @POST
    @Path("/temp")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Schema(description = "upload new temporary binary object")
    public LakeHttpResponse<String> newTemp(
        @HeaderParam("Authorization") String bearer,
        InputStream output);

    @DELETE
    @Path("/temp/{cid}")
    @Schema(description = "delete temporary binary daya")
    public LakeHttpResponse<String> delTemp(
        @PathParam("cid") Long cid,
        @HeaderParam("Authorization") String bearer);

}
