package org.usth.ict.ulake.user.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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

import java.util.HashMap;

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

    @GET
    @Path("/stats")
    @Operation(summary = "Some statistics")
    @RolesAllowed({ "User", "Admin" })
    public Response stats() {
        HashMap<String, Integer> ret = new HashMap<>();
        ret.put("departments", (int) repo.count());
        return response.build(200, "", ret);
    }
}
