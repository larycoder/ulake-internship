package org.usth.ict.ulake.dashboard.extension;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.usth.ict.ulake.dashboard.model.ObjectModel;
import org.usth.ict.ulake.dashboard.model.extension.ExtensionModel;

@Path("/api")
@RegisterRestClient(configKey = "core-api")
@Produces(MediaType.APPLICATION_JSON)
public interface CoreService {
    @GET
    @Path("/object")
    public ExtensionModel<List<ObjectModel>> getListObject(
        @HeaderParam("Authorization") String bearer);

    @GET
    @Path("/object/{cid}")
    public ExtensionModel<ObjectModel> getObjectInfo(
        @PathParam("cid") String cid,
        @HeaderParam("Authorization") String bearer);

    @GET
    @Path("/object/{cid}/data")
    public InputStream getObjectData(
        @PathParam("cid") String cid,
        @HeaderParam("Authorization") String bearer);
}
