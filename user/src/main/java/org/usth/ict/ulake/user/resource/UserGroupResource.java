package org.usth.ict.ulake.user.resource;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.user.model.User;
import org.usth.ict.ulake.user.model.UserGroup;
import org.usth.ict.ulake.user.persistence.UserGroupRepository;
import org.usth.ict.ulake.user.persistence.UserRepository;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user/group")
@Produces(MediaType.APPLICATION_JSON)
public class UserGroupResource {
    private static final Logger log = LoggerFactory.getLogger(UserGroupResource.class);

    @Inject
    LakeHttpResponse response;

    @Inject
    UserGroupRepository repo;

    @GET
    @Operation(summary = "List all user groups")
    public Response all() {
        return response.build(200, "", repo.listAll());
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get one user group info")
    public Response one(@PathParam("id") @Parameter(description = "User group id to search") Long id) {
        return response.build(200, null, repo.findById(id));
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a new user group")
    public Response post(@RequestBody(description = "New user group info to save") UserGroup entity) {
        repo.persist(entity);
        return response.build(200, "", entity);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update an existing user group")
    public Response update(@PathParam("id") @Parameter(description = "User id to update") Long id,
                           @RequestBody(description = "New user group info to update") UserGroup newEntity) {
        UserGroup entity = repo.findById(id);
        if (entity == null) {
            return response.build(404);
        }
        if (!Utils.isEmpty(newEntity.name)) entity.name = newEntity.name;

        // TODO: allow update department, group
        repo.persist(entity);
        return response.build(200);
    }
}
