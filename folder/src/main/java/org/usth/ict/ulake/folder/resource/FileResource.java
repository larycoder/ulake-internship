package org.usth.ict.ulake.folder.resource;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.folder.model.UserFile;
import org.usth.ict.ulake.folder.persistence.FileRepository;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/file")
@Tag(name = "File")
@Produces(MediaType.APPLICATION_JSON)
public class FileResource {
    @Inject
    FileRepository repo;

    @Inject
    LakeHttpResponse response;

    @GET
    @Operation(summary = "List all files")
    public Response all() {
        return response.build(200, "", repo.listAll());
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a single file info")
    public Response one(@PathParam("id") Long id) {
        return response.build(200, null, repo.findById(id));
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a new file info")
    public Response post(UserFile entity) {
        repo.persist(entity);
        return response.build(200, "", entity);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update a file info")
    public Response update(@PathParam("id") Long id, UserFile newEntity) {
        return response.build(405);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete a file info")
    public Response delete(@PathParam("id") Long id) {
        UserFile entity = repo.findById(id);
        if (entity == null) {
            return response.build(404);
        }
        repo.delete(entity);
        return response.build(200);
    }
}