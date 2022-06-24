package org.usth.ict.ulake.common.service;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.user.AuthModel;

@Path("/api")
@RegisterRestClient(configKey = "user-api")
@RegisterProvider(value = LakeServiceExceptionMapper.class)
@Produces(MediaType.APPLICATION_JSON)
public interface UserService {
    @GET
    @Path("/user/stats")
    public LakeHttpResponse getStats(
        @HeaderParam("Authorization") String bearer);

    @POST
    @Path("/auth/login")
    public LakeHttpResponse getToken(AuthModel auth);
}
