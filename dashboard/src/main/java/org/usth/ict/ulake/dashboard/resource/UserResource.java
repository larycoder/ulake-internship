package org.usth.ict.ulake.dashboard.resource;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.usth.ict.ulake.dashboard.extension.UserService;
import org.usth.ict.ulake.dashboard.model.AuthModel;
import org.usth.ict.ulake.dashboard.model.extension.ExtensionModel;

@Path("/api")
public class UserResource {

    @Inject
    @RestClient
    UserService userSvc;

    @POST
    @Path("/user/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ExtensionModel<String> login(AuthModel auth) {
        return userSvc.getToken(auth);
    }
}
