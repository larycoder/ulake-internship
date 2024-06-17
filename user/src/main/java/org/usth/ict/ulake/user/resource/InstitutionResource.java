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
import org.usth.ict.ulake.user.model.Institution;
import org.usth.ict.ulake.user.persistence.InstitutionRepository;

import java.util.HashMap;

@Path("/institution")
@Tag(name = "Institutions")
@Produces(MediaType.APPLICATION_JSON)
public class InstitutionResource {
    private static final Logger log = LoggerFactory.getLogger(InstitutionResource.class);

    @Inject
    LakeHttpResponse response;

    @Inject
    InstitutionRepository repo;

    @GET
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "List all institutions")
    public Response all() {
        return response.build(200, "", repo.listAll());
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Get one institution info")
    public Response one(@PathParam("id") @Parameter(description = "Institution id to search") Long id) {
        return response.build(200, null, repo.findById(id));
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "Admin" })
    @Operation(summary = "Create a new institution")
    public Response post(@RequestBody(description = "New institution info to save") Institution entity) {
        repo.persist(entity);
        return response.build(200, "", entity);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "Admin" })
    @Operation(summary = "Update an existing institution")
    public Response update(@PathParam("id") @Parameter(description = "Institution id to update") Long id,
                           @RequestBody(description = "New institution info to update") Institution newEntity) {
        Institution entity = repo.findById(id);
        if (entity == null) {
            return response.build(404);
        }
        if (!Utils.isEmpty(newEntity.name)) entity.name = newEntity.name;
        // TODO: allow update department
        repo.persist(entity);
        return response.build(200);
    }

    @GET
    @Path("/stats")
    @Operation(summary = "Some statistics")
    @RolesAllowed({ "User", "Admin" })
    public Response stats() {
        HashMap<String, Integer> ret = new HashMap<>();
        ret.put("institutions", (int) repo.count());
        return response.build(200, "", ret);
    }
}
