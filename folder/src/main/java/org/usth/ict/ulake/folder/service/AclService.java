package org.usth.ict.ulake.folder.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.usth.ict.ulake.acl.model.AclModel;
import org.usth.ict.ulake.common.model.LakeHttpResponse;

// TODO: move base uri to application configuration
@Path("/api")
@RegisterRestClient(baseUri="http://acl.ulake.sontg.net")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface AclService {
    @POST
    @Path("/acl/permission")
    public LakeHttpResponse isAllowed(
        @HeaderParam("Authorization") String bearer, AclModel acl);
}
