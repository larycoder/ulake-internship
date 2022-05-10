package org.usth.ict.ulake.common.service;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.util.HashMap;

@Path("/api")
@RegisterRestClient(configKey = "user-api")
@Produces(MediaType.APPLICATION_JSON)
public interface UserService {
    @GET
    @Path("/user/stats")
    public HashMap<String, Integer> getStats(
            @HeaderParam("Authorization") String bearer);
}
