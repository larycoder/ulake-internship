package org.usth.ict.ulake.common.service;

import java.util.List;

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
import org.usth.ict.ulake.common.model.user.User;
import org.usth.ict.ulake.common.model.user.UserSearchQuery;

@Path("/api")
@RegisterRestClient(configKey = "user-api")
@RegisterProvider(value = LakeServiceExceptionMapper.class)
@Produces(MediaType.APPLICATION_JSON)
public interface UserService {
    @GET
    @Path("/user/stats")
    public LakeHttpResponse<Object> getStats(
        @HeaderParam("Authorization") String bearer);

    @POST
    @Path("/auth/login")
    public LakeHttpResponse<Object> getToken(AuthModel auth);

    @POST
    @Path("/user/search")
    public LakeHttpResponse<List<User>> search(
        @HeaderParam("Authorization") String bearer, UserSearchQuery query);
}
