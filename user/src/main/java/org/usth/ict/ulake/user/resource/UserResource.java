package org.usth.ict.ulake.user.resource;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.log.LogModel;
import org.usth.ict.ulake.common.service.LogService;
import org.usth.ict.ulake.user.model.User;
import org.usth.ict.ulake.user.model.UserSearchQuery;
import org.usth.ict.ulake.user.persistence.UserRepository;

import io.quarkus.elytron.security.common.BcryptUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Path("/user")
@Tag(name = "Users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private static final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Inject
    LakeHttpResponse response;

    @Inject
    UserRepository repo;

    @Inject
    JsonWebToken jwt;

    @Inject
    @RestClient
    LogService logService;

    @GET
    @Operation(summary = "List all users")
    @RolesAllowed({ "Admin" })
    public Response all() {
        return response.build(200, "", repo.listAll());
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Get one user info")
    public Response one(@HeaderParam("Authorization") String bearer,
                        @PathParam("id") @Parameter(description = "User id to search") String id) {
        if (Utils.isNumeric(id)) {
            // TODO : only allow himself if not admin
            logService.post(bearer, new LogModel("Query", "Get user info for " + id));
            return response.build(200, null, repo.findById(Long.parseLong(id)));
        }
        else {
            // TODO : only allow himself if not admin
            String idArr[] = id.split(",");
            List<Long> ids = Arrays.asList(idArr).stream()
                .filter(idStr -> Utils.isNumeric(idStr))
                .mapToLong(Long::parseLong)
                .boxed()
                .collect(Collectors.toList());
            UserSearchQuery query = new UserSearchQuery();
            query.ids = ids;
            logService.post(bearer, new LogModel("Query", "Get many ids: " + ids));
            return search(query);
        }
    }


    @POST
    @Path("/search")
    @RolesAllowed({ "Admin" })
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
    public Response post(@HeaderParam("Authorization") String bearer,
            @RequestBody(description = "New user info to save") User entity) {
        if (entity == null) {
            return response.build(400, "", entity);
        }

        // check existence
        var exist = repo.find("userName", entity.userName).firstResult();
        if (exist != null) {
            return response.build(409, "", entity);
        }

        entity.isAdmin = false;
        entity.password = BcryptUtil.bcryptHash(entity.password);
        entity.registerTime = new Date().getTime()/1000;
        repo.persist(entity);

        logService.post(bearer, new LogModel("Insert", "Created new user " + entity.userName));
        return response.build(200, "", entity);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @RolesAllowed({ "User", "Admin" })
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update an existing user")
    public Response update(@HeaderParam("Authorization") String bearer,
                           @PathParam("id") @Parameter(description = "User id to update") Long id,
                           @RequestBody(description = "New user info to update") User newEntity) {
        User entity = repo.findById(id);
        if (entity == null) {
            return response.build(404);
        }

        // only admin token could modify admin field
        if (!jwt.getGroups().contains("Admin")) {
            newEntity.isAdmin = entity.isAdmin;
        }

        if (!Utils.isEmpty(newEntity.firstName)) entity.firstName = newEntity.firstName;
        if (!Utils.isEmpty(newEntity.lastName)) entity.lastName = newEntity.lastName;
        if (!Utils.isEmpty(newEntity.email)) entity.email = newEntity.email;
        if (!Utils.isEmpty(newEntity.password)) entity.password = newEntity.password;
        if (newEntity.registerTime != 0) entity.registerTime = newEntity.registerTime;

        // TODO : only allow himself if not admin

        logService.post(bearer, new LogModel("Update", "Updated user " + id));

        // TODO: allow update department, group
        repo.persist(entity);
        return response.build(200);
    }

    @GET
    @Path("/stats")
    @Operation(summary = "Some statistics")
    @RolesAllowed({ "Admin" })
    public Response stats() {
        HashMap<String, Object> ret = new HashMap<>();
        HashMap<String, Integer> regs = new HashMap<>();
        var stats = repo.getUserRegistrationByDate();
        Integer count = (int) repo.count();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        for (var stat: stats) {
            String text = df.format(stat.getDate());
            regs.put(text, stat.getCount());
        }
        ret.put("regs", regs);
        ret.put("count", count);
        return response.build(200, "", ret);
    }
}
