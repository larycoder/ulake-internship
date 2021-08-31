package org.usth.ict.ulake.user.resource;

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
import org.usth.ict.ulake.user.model.Department;
import org.usth.ict.ulake.user.persistence.DepartmentRepository;

@Path("/department")
@Tag(name = "Departments")
@Produces(MediaType.APPLICATION_JSON)
public class DepartmentResource {
    private static final Logger log = LoggerFactory.getLogger(DepartmentResource.class);

    @Inject
    LakeHttpResponse response;

    @Inject
    DepartmentRepository repo;

    @GET
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "List all department")
    public Response all() {
        return response.build(200, "", repo.listAll());
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Get one department info")
    public Response one(@PathParam("id") @Parameter(description = "Department id to search") Long id) {
        return response.build(200, null, repo.findById(id));
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "Admin" })
    @Operation(summary = "Create a new department")
    public Response post(@RequestBody(description = "New department info to save") Department entity) {
        repo.persist(entity);
        return response.build(200, "", entity);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "Admin" })
    @Operation(summary = "Update an existing department")
    public Response update(@PathParam("id") @Parameter(description = "Department id to update") Long id,
                           @RequestBody(description = "New department info to update") Department newEntity) {
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
