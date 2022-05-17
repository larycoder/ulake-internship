package org.usth.ict.ulake.dashboard.extension;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.dashboard.model.AuthModel;

@Path("/api")
@RegisterRestClient(configKey = "user-api")
@Produces(MediaType.APPLICATION_JSON)
public interface UserService {
    @POST
    @Path("/auth/login")
    public LakeHttpResponse getToken(AuthModel auth);
}
