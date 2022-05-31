package org.usth.ict.ulake.acl.resource;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.usth.ict.ulake.acl.model.GroupFolderAcl;
import org.usth.ict.ulake.acl.persistence.GroupFolderAclRepo;
import org.usth.ict.ulake.common.model.LakeHttpResponse;

@Path("/group/acl/folder")
@Produces(MediaType.APPLICATION_JSON)
public class GroupFolderAclResource {
    @Inject
    LakeHttpResponse response;

    @Inject
    GroupFolderAclRepo repo;

    @GET
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "list all ACL without permission")
    public Response list() {
        return response.build(200, null, repo.listAcl());
    }

    @GET
    @Path("/all")
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "list all ACL with permission")
    public Response all() {
        return response.build(200, null, repo.findAll());
    }

    @POST
    @Transactional
    @RolesAllowed({"User", "User", "Admin"})
    @Operation(summary = "add new permission")
    @APIResponses({
        @APIResponse(name = "400", responseCode = "400", description = "Invalid passing"),
        @APIResponse(name = "409", responseCode = "409", description = "already existed")
    })
    public Response post(GroupFolderAcl acl) {
        if (repo.hasAcl(acl)) {
            return response.build(409, "ACL is already existed");
        } else {
            repo.persist(acl);
            return response.build(200, null, acl);
        }
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "delete permission of object")
    public Response del(
        @PathParam("id") @Parameter(description = "Permission id") Long id) {
        return response.build(200, null, repo.deleteById(id));
    }
}
