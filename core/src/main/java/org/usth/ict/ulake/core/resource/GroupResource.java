package org.usth.ict.ulake.core.resource;

import java.util.UUID;

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

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.core.backend.impl.Hdfs;
import org.usth.ict.ulake.core.model.LakeGroup;
import org.usth.ict.ulake.core.persistence.GroupRepository;
import org.usth.ict.ulake.core.persistence.ObjectRepository;

@Path("/group")
@Tag(name = "Object Groups")
@Produces(MediaType.APPLICATION_JSON)
public class GroupResource {
    private static final Logger log = LoggerFactory.getLogger(GroupResource.class);

    @Inject
    Hdfs fs;

    @Inject
    GroupRepository repo;

    @Inject
    ObjectRepository objRepo;

    @Inject
    LakeHttpResponse response;

    @GET
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "List all object groups")
    public Response all() {
        return response.build(200, null, repo.listAll());
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Get one object group info")
    public Response one(
        @PathParam("id")
        @Parameter(description = "Object group id to search") Long id) {
        return response.build(200, null, repo.findById(id));
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Create a new object group")
    public Response post(
        @RequestBody(description = "New object group info to save")
        LakeGroup entity) {
        // TODO: verify owner ship
        entity.gid = UUID.randomUUID().toString();
        repo.persist(entity);
        return response.build(200, "", entity);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Update an existing object group")
    public Response update(
        @PathParam("id")
        @Parameter(description = "Object group id to update") Long id,
        @RequestBody(description = "New object group info to update")
        LakeGroup newEntity) {
        // TODO: verify owner ship
        LakeGroup entity = repo.findById(id);
        if (entity == null) {
            return response.build(404);
        }
        if (!Utils.isEmpty(newEntity.name)) entity.name = newEntity.name;
        if (!Utils.isEmpty(newEntity.gid)) entity.gid = newEntity.gid;
        if (!Utils.isEmpty(newEntity.parentGid)) entity.parentGid = newEntity.parentGid;
        if (!Utils.isEmpty(newEntity.extra)) entity.extra = newEntity.extra;
        if (!Utils.isEmpty(newEntity.tags)) entity.tags = newEntity.tags;
        repo.persist(entity);
        return response.build(200, null, entity);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "Admin" })
    @Operation(summary = "Update an existing object group")
    public Response delete (
        @PathParam("id")
        @Parameter(description = "Object group id to delete")
        Long id) {
        // TODO: verify owner ship
        LakeGroup entity = repo.findById(id);
        if (entity == null) {
            return response.build(404);
        }
        repo.delete(entity);
        return response.build(200);
    }
}
