package org.usth.ict.ulake.core.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.core.model.LakeDataset;
import org.usth.ict.ulake.core.persistence.DatasetRepository;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

@Path("/dataset")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "[WIP] Dataset")
public class DatasetResource {
    private static final Logger log = LoggerFactory.getLogger(DatasetResource.class);

    @Inject
    DatasetRepository repo;

    @Inject
    LakeHttpResponse response;

    @GET
    @Operation(summary = "List all datasets")
    @RolesAllowed({ "User", "Admin" })
    public Response all() {
        return response.build(200, "", repo.listAll());
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Show info of one dataset")
    public Response one(@PathParam("id") Long id) {
        return response.build(200, null, repo.findById(id));
    }

    @POST
    @Transactional
    @RolesAllowed({ "User", "Admin" })
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a new dataset")
    public Response post(LakeDataset entity) {
        repo.persist(entity);
        return response.build(200, "", entity);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @RolesAllowed({ "User", "Admin" })
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update an existing dataset")
    public Response update(@PathParam("id") @Parameter(description = "Dataset id to update") Long id, 
                            @RequestBody(description = "New dataset info to update")LakeDataset newEntity) {
        LakeDataset entity = repo.findById(id);
        if (entity == null) {
            return response.build(404);
        }
        if (!Utils.isEmpty(newEntity.description)) entity.description = newEntity.description;
        if (!Utils.isEmpty(newEntity.name)) entity.name = newEntity.name;
        if (!Utils.isEmpty(newEntity.source)) entity.source = newEntity.source;
        if (!Utils.isEmpty(newEntity.licence)) entity.licence = newEntity.licence;
        if (!Utils.isEmpty(newEntity.tags)) entity.tags = newEntity.tags;
        repo.persist(entity);
        return response.build(200);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "Admin" })
    @Operation(summary = "Delete an existing dataset")
    public Response delete(@PathParam("id") Long id) {
        LakeDataset entity = repo.findById(id);
        if (entity == null) {
            return response.build(404);
        }
        repo.delete(entity);
        return response.build(200);
    }
}
