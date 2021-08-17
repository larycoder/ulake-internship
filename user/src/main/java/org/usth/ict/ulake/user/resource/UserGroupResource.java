package org.usth.ict.ulake.user.resource;

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
    public Response all() {
        return response.build(200, "", repo.listAll());
    }

    @GET
    @Path("/{id}")
    public Response one(@PathParam("id") Long id) {
        return response.build(200, null, repo.findById(id));
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(UserGroup entity) {
        repo.persist(entity);
        return response.build(200, "", entity);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") Long id, UserGroup newEntity) {
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
