package org.usth.ict.ulake.folder.resource;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.folder.model.UserFolder;
import org.usth.ict.ulake.folder.persistence.FolderRepository;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/folder")
@Tag(name = "Folder")
@Produces(MediaType.APPLICATION_JSON)
public class FolderResource {
    @Inject
    FolderRepository repo;

    @Inject
    LakeHttpResponse response;

    @GET
    @Operation(summary = "List all folders")
    public Response all() {
        return response.build(200, "", repo.listAll());
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get one folder")
    public Response one(@PathParam("id") @Parameter(description = "Folder id to search") Long id) {
        return response.build(200, null, repo.findById(id));
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a new folder")
    public Response post(@RequestBody(description = "Folder to save") UserFolder entity) {
        repo.persist(entity);
        return response.build(200, "", entity);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update a folder information")
    public Response update(@PathParam("id") @Parameter(description = "Folder id to update") Long id,
                           @RequestBody(description = "New folder information") UserFolder newData) {
        return response.build(405);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete a folder")
    public Response delete(@PathParam("id") @Parameter(description = "Folder id to delete") Long id) {
        UserFolder entity = repo.findById(id);
        if (entity == null) {
            return response.build(404);
        }
        repo.delete(entity);
        return response.build(200);
    }
}