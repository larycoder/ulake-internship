package org.usth.ict.ulake.dashboard.extension;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.dashboard.model.ObjectFormModel;

@Path("/api")
@RegisterRestClient(configKey = "core-api")
@Produces(MediaType.APPLICATION_JSON)
public interface CoreService {
    @GET
    @Path("/object")
    @Schema(description = "list all object of user")
    public LakeHttpResponse objectList(
        @HeaderParam("Authorization") String bearer);

    @GET
    @Path("/object/{cid}")
    @Schema(description = "get object information")
    public LakeHttpResponse objectInfo(
        @PathParam("cid") String cid,
        @HeaderParam("Authorization") String bearer);

    @GET
    @Path("/object/{cid}/data")
    @Schema(description = "load binary object from core")
    public InputStream objectData(
        @PathParam("cid") String cid,
        @HeaderParam("Authorization") String bearer);

    @POST
    @Path("/object")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Schema(description = "upload new object")
    public LakeHttpResponse newObject(
        @HeaderParam("Authorization") String bearer,
        @MultipartForm ObjectFormModel output);
}
