package org.usth.ict.ulake.user.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.user.model.Department;
import org.usth.ict.ulake.user.model.User;
import org.usth.ict.ulake.user.persistence.DepartmentRepository;
import org.usth.ict.ulake.user.persistence.UserRepository;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/department")
@Produces(MediaType.APPLICATION_JSON)
public class DepartmentResource {
    private static final Logger log = LoggerFactory.getLogger(DepartmentResource.class);

    @Inject
    LakeHttpResponse response;

    @Inject
    DepartmentRepository repo;

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
    public Response post(Department entity) {
        repo.persist(entity);
        return response.build(200, "", entity);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") Long id, Department newEntity) {
        Department entity = repo.findById(id);
        if (entity == null) {
            return response.build(404);
        }
        if (!Utils.isEmpty(newEntity.name)) entity.name = newEntity.name;
        if (!Utils.isEmpty(newEntity.address)) entity.address = newEntity.address;
        // TODO: allow update institution
        repo.persist(entity);
        return response.build(200);
    }
}
