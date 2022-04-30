package org.usth.ict.ulake.admin.service;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/api")
@RegisterRestClient(configKey = "folder-api")
@Produces(MediaType.APPLICATION_JSON)
public interface FolderService {
    @GET
    @Path("/file")
    // nothing yet.
    public List<String> getListFile(
            @HeaderParam("Authorization") String bearer);
}
