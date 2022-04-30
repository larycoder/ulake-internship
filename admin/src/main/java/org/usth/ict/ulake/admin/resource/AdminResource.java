package org.usth.ict.ulake.admin.resource;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.usth.ict.ulake.admin.persistence.AdminRepository;
import org.usth.ict.ulake.common.model.LakeHttpResponse;

import java.util.Date;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {
    @Inject
    LakeHttpResponse response;

    @Inject
    AdminRepository repo;

    @GET
    //@RolesAllowed({"User", "Admin"})
    @Operation(summary = "Nothing yet.")
    public Response all() {
        return response.build(200, null, new Date());
    }
}
