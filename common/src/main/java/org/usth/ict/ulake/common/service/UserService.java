package org.usth.ict.ulake.common.service;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.user.AuthModel;
import org.usth.ict.ulake.common.model.user.User;
import org.usth.ict.ulake.common.model.user.UserSearchQuery;
import org.usth.ict.ulake.common.model.user.UserSearchQueryV2;

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

    @POST
    @Path("/user/search/v2")
    public LakeHttpResponse<List<User>> searchV2(
        @HeaderParam("Authorization") String bearer, UserSearchQueryV2 query);

}
