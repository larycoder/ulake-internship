package org.usth.ict.query.extension;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.usth.ict.query.model.AuthModel;
import org.usth.ict.query.model.extension.ExtensionModel;

@Path("/api")
@RegisterRestClient(configKey = "user-api")
@Produces(MediaType.APPLICATION_JSON)
public interface UserService {
    @POST
    @Path("/auth/login")
    public ExtensionModel<String> getToken(AuthModel auth);
}
