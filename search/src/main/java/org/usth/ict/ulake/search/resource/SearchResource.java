package org.usth.ict.ulake.search.resource;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.usth.ict.ulake.common.model.LakeHttpResponse;

@Path("/search")
@Produces(MediaType.APPLICATION_JSON)
public class SearchResource {
    @Inject
    LakeHttpResponse response;

    @GET
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "health check")
    public Response health() {
        return response.build(200, null);
    }
}
