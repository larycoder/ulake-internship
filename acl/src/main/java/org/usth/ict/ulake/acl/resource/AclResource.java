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

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.usth.ict.ulake.acl.model.AclModel;
import org.usth.ict.ulake.acl.persistence.AclRepository;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.PermissionModel;

@Path("/acl")
@Produces(MediaType.APPLICATION_JSON)
public class AclResource {
    @Inject
    LakeHttpResponse response;

    @Inject
    AclRepository repo;

    @Inject
    JsonWebToken jwt;

    @GET
    @Operation(summary = "health check")
    public Response health() {
        return response.build(200, null);
    }

    @GET
    @RolesAllowed({"System", "Admin"})
    @Operation(summary = "list all acl")
    public Response all() {
        return response.build(200, null, repo.findAll());
    }

    @POST
    @Transactional
    @RolesAllowed({"System", "User", "Admin"})
    @Operation(summary = "add new permission")
    @APIResponses({
        @APIResponse(name = "400", responseCode = "400", description = "Invalid ACL passing"),
        @APIResponse(name = "409", responseCode = "409", description = "ACL is already existed")
    })
    public Response post(AclModel acl) {
        // TODO: ACL for new permission granting

        if (!PermissionModel.isPermission(acl.getPermission())) {
            return response.build(400, "Unrecognized permission");
        } else if (!isBoolFlag(acl.getIsFolder())) {
            return response.build(400, "Invalid isFolder value, must in [0, 1]");
        } else if (!isBoolFlag(acl.getIsGroup())) {
            return response.build(400, "Invalid isGroup value, must in [0, 1]");
        } else if (repo.hasAcl(acl)) {
            return response.build(409, "ACL is already existed");
        } else {
            repo.persist(acl);
            return response.build(200, null, acl);
        }
    }

    @POST
    @Path("/permission")
    @RolesAllowed({"System", "Admin"})
    @Operation(summary = "assert permission of object")
    @APIResponses({
        @APIResponse(name = "400", responseCode = "400", description = "Invalid ACL passing"),
    })
    public Response permission(AclModel acl) {
        if (!PermissionModel.isPermission(acl.getPermission())) {
            return response.build(400, "Unrecognized permission");
        } else if (!isBoolFlag(acl.getIsFolder())) {
            return response.build(400, "Invalid isFolder value, must in [0, 1]");
        } else if (!isBoolFlag(acl.getIsGroup())) {
            return response.build(400, "Invalid isGroup value, must in [0, 1]");
        } else {
            return response.build(200, null, repo.hasAcl(acl));
        }
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    @RolesAllowed({"System", "Admin"})
    @Operation(summary = "delete permission of object")
    public Response del(
        @PathParam("id") @Parameter(description = "Permission id to delete") Long id) {
        return response.build(200, null, repo.deleteById(id));
    }

    /**
     * Assert value is char(1) in range (0, 1)
     * @param value assertion value
     * @return true if value is valid else false
    */
    private boolean isBoolFlag(String value) {
        return (value.equals("0") || value.equals("1"));
    }
}
