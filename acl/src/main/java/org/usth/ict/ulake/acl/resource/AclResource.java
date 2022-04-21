package org.usth.ict.ulake.acl.resource;


import java.util.ArrayList;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.logging.Logger;
import org.usth.ict.ulake.acl.model.AclModel;
import org.usth.ict.ulake.acl.persistence.AclRepository;
import org.usth.ict.ulake.common.model.LakeHttpResponse;

@Path("/acl")
@Produces(MediaType.APPLICATION_JSON)
public class AclResource {
    private static final Logger log = Logger.getLogger(AclResource.class);

    @Inject
    LakeHttpResponse response;

    @Inject
    AclRepository repo;

    @Inject
    JsonWebToken jwt;

    @GET
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "health check")
    public Response health() {
        return response.build(200, null);
    }

    @GET
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "list all acl")
    public Response all() {
        return response.build(200, null, repo.findAll());
    }

    @POST
    @Transactional
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "add new ACL permission")
    public Response post(AclModel acl) {
        // debug information
        String obj = "{User: %d, Object: %d, Permission: %d}";
        Long u = acl.getUserId();
        Long o = acl.getObjectId();
        Integer p = acl.getPermission();
        obj = String.format(obj, u, o, p);

        if (repo.hasAcl(acl)) {
            log.infof("Acl " + obj + " is already existed");
            return response.build(400, "ACL is already existed");
        }

        repo.persist(acl);
        log.infof("New acl is created: " + obj);
        return response.build(200, null, acl);
    }

    @GET
    @Path("/file/{objectId}/{permission}")
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "check ACL permission for file")
    public Response file(Long objectId, Integer permission) {
        Long userId = Long.parseLong(jwt.getName());

        AclModel acl = new AclModel();
        acl.setUserId(userId);
        acl.setObjectId(objectId);
        acl.setPermission(permission);
        acl.setIsFolder("0");

        return response.build(200, null, repo.hasAcl(acl));
    }

    @GET
    @Path("/folder/{objectId}/{permission}")
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "check ACL permission for folder")
    public Response folder(Long objectId, Integer permission) {
        Long userId = Long.parseLong(jwt.getName());

        AclModel acl = new AclModel();
        acl.setUserId(userId);
        acl.setObjectId(objectId);
        acl.setPermission(permission);
        acl.setIsFolder("1");

        return response.build(200, null, repo.hasAcl(acl));
    }
}
