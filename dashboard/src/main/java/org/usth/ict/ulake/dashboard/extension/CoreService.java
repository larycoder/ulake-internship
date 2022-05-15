package org.usth.ict.ulake.dashboard.extension;

import java.io.InputStream;
import java.util.List;

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
import org.usth.ict.ulake.dashboard.model.ObjectFormModel;
import org.usth.ict.ulake.dashboard.model.ObjectModel;
import org.usth.ict.ulake.dashboard.model.extension.ExtensionModel;

@Path("/api")
@RegisterRestClient(configKey = "core-api")
@Produces(MediaType.APPLICATION_JSON)
public interface CoreService {
    @GET
    @Path("/object")
    @Schema(description = "list all object of user")
    public ExtensionModel<List<ObjectModel>> getListObject(
        @HeaderParam("Authorization") String bearer);

    @GET
    @Path("/object/{cid}")
    @Schema(description = "get object information")
    public ExtensionModel<ObjectModel> getObjectInfo(
        @PathParam("cid") String cid,
        @HeaderParam("Authorization") String bearer);

    @GET
    @Path("/object/{cid}/data")
    @Schema(description = "load binary object from core")
    public InputStream getObjectData(
        @PathParam("cid") String cid,
        @HeaderParam("Authorization") String bearer);

    @POST
    @Path("/object")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Schema(description = "upload new object")
    public ExtensionModel<ObjectModel> newObject(
        @HeaderParam("Authorization") String bearer,
        @MultipartForm ObjectFormModel output);
}
