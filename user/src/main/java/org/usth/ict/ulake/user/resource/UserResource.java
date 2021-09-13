package org.usth.ict.ulake.user.resource;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.user.model.User;
import org.usth.ict.ulake.user.model.UserSearchQuery;
import org.usth.ict.ulake.user.persistence.UserRepository;

import io.quarkus.elytron.security.common.BcryptUtil;

@Path("/user")
@Tag(name = "Users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private static final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Inject
    LakeHttpResponse response;

    @Inject
    UserRepository repo;

    @GET
    @Operation(summary = "List all users")
    @RolesAllowed({ "User", "Admin" })
    public Response all() {
        return response.build(200, "", repo.listAll());
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Get one user info")
    public Response one(@PathParam("id") @Parameter(description = "User id to search") Long id) {
        return response.build(200, null, repo.findById(id));
    }

    @POST
    @Path("/search")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Search for users")
    public Response search(@RequestBody(description = "Query to perform search for users") UserSearchQuery query) {
        var results = repo.search(query);
        if (results.isEmpty()) {
            return response.build(404);
        }
        return response.build(200, null, results);
    }


    @POST
    @Transactional
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a new user")
    public Response post(@RequestBody(description = "New user info to save") User entity) {
        if (entity == null) {
            return response.build(400, "", entity);    
        }

        // check existence
        User exist = (User) repo.find("userName", entity.userName);
        if (exist == null) {
            return response.build(409, "", entity);
        }


        log.info("POSTING new user");
        entity.password = BcryptUtil.bcryptHash(entity.password);
        repo.persist(entity);
        return response.build(200, "", entity);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @RolesAllowed({ "User", "Admin" })
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update an existing user")
    public Response update(@PathParam("id") @Parameter(description = "User id to update") Long id,
                           @RequestBody(description = "New user info to update") User newEntity) {
        User entity = repo.findById(id);
        if (entity == null) {
            return response.build(404);
        }
        if (!Utils.isEmpty(newEntity.firstName)) entity.firstName = newEntity.firstName;
        if (!Utils.isEmpty(newEntity.lastName)) entity.lastName = newEntity.lastName;
        if (!Utils.isEmpty(newEntity.email)) entity.email = newEntity.email;
        if (!Utils.isEmpty(newEntity.password)) entity.password = newEntity.password;
        if (newEntity.registerTime != 0) entity.registerTime = newEntity.registerTime;

        // TODO: allow update department, group
        repo.persist(entity);
        return response.build(200);
    }
}
