package org.usth.ict.ulake.common.service;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.usth.ict.ulake.common.model.LakeHttpResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/api")
@RegisterRestClient(configKey = "user-api")
@Produces(MediaType.APPLICATION_JSON)
public interface UserService {
    @GET
    @Path("/user/stats")
    public LakeHttpResponse getStats(
            @HeaderParam("Authorization") String bearer);
}
