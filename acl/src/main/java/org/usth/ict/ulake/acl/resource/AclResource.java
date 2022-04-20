package org.usth.ict.ulake.acl.resource;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.hibernate.exception.ConstraintViolationException;
import org.usth.ict.ulake.acl.model.AclModel;
import org.usth.ict.ulake.acl.persistence.AclRepository;
import org.usth.ict.ulake.common.model.LakeHttpResponse;

@Path("/acl")
@Tag(name="Acl")
@Produces(MediaType.APPLICATION_JSON)
public class AclResource {
    @Inject
    LakeHttpResponse response;

    @Inject
    AclRepository repo;

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
        if(repo.hasAcl(acl)) {
            return response.build(400, "Acl is already existed");
        }
        repo.persist(acl);
        return response.build(200, null, acl);
    }
}
