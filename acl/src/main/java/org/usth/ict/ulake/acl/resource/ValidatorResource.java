package org.usth.ict.ulake.acl.resource;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.usth.ict.ulake.acl.persistence.AclRepo;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.acl.Acl;
import org.usth.ict.ulake.common.model.acl.macro.FileType;

@Path("/acl/validation")
@Produces(MediaType.APPLICATION_JSON)
public class ValidatorResource {
    @Inject
    LakeHttpResponse<Object> resp;

    @Inject
    AclRepo repo;

    @POST
    @Path("/{fileType}")
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "assert permission of file")
    @APIResponses({
        @APIResponse(name = "400", responseCode = "400", description = "Invalid ACL passing"),
        @APIResponse(name = "200", responseCode = "200", description = "OK"),
    })
    public Response validateFile(@PathParam("fileType") FileType type, Acl acl) {
        if (!repo.findAcl(type, acl).isEmpty())
            return resp.build(200, null, true);
        else
            return resp.build(200, null, false);
    }
}
