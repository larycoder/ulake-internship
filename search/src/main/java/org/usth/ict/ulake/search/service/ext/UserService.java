package org.usth.ict.ulake.search.service.ext;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.search.service.SearchService;
import org.usth.ict.ulake.user.model.UserSearchQuery;

@Path("/api")
@RegisterRestClient(configKey = "user-api")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface UserService extends SearchService<UserSearchQuery> {
    @POST
    @Override
    @Path("/search")
    public LakeHttpResponse search(
        @HeaderParam("Authorization") String bearer,
        UserSearchQuery query);
}
