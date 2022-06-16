package org.usth.ict.ulake.folder.resource;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.folder.model.UserFolder;
import org.usth.ict.ulake.folder.persistence.FileRepository;
import org.usth.ict.ulake.folder.persistence.FolderRepository;

@Path("/folder")
@Tag(name = "Folder")
@Produces(MediaType.APPLICATION_JSON)
public class FolderResource {
    @Inject
    JsonWebToken jwt;

    @Inject
    FolderRepository repo;

    @Inject
    FileRepository fileRepo;

    @Inject
    LakeHttpResponse response;

    @GET
    @RolesAllowed({ "Admin" })
    @Operation(summary = "List all folders")
    public Response all() {
        return response.build(200, "", repo.listAll());
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Get one folder")
    public Response one(
        @PathParam("id")
        @Parameter(description = "Folder id to search") Long id) {
        var folder = repo.findById(id);

        if (folder == null)
            return response.build(404, "Folder not found");

        if (verifyACL(folder, Long.parseLong(jwt.getName()), jwt.getGroups()))
            return response.build(403);

        return response.build(200, null, folder);
    }

    /**
     * Provide root folders and files in a virtual folder
     * */
    @GET
    @Path("/root")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "List root folder")
    public Response root() {
        var ownerId = Long.parseLong(jwt.getClaim(Claims.sub));
        UserFolder root = new UserFolder();
        root.ownerId = ownerId;
        if (jwt.getGroups().contains("Admin")) {
            root.subFolders = repo.listRoot();
            root.files = fileRepo.listRoot();
        } else {
            root.subFolders = repo.listRoot(ownerId);
            root.files = fileRepo.listRoot(ownerId);
        }
        return response.build(200, null, root);
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Create a new folder")
    public Response post(
        @RequestBody(description = "Folder to save") UserFolder entity) {
        if (verifyACL(entity, Long.parseLong(jwt.getName()), jwt.getGroups()))
            return response.build(403);

        repo.persist(entity);
        return response.build(200, "", entity);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Update a folder information")
    public Response update(
        @PathParam("id")
        @Parameter(description = "Folder id to update")
        Long id,
        @RequestBody(description = "New folder information")
        UserFolder data) {
        UserFolder entity = repo.findById(id);

        if (entity == null)
            return response.build(404, "Folder not found");

        if (verifyACL(entity, Long.parseLong(jwt.getName()), jwt.getGroups()))
            return response.build(403);

        if (!Utils.isEmpty(data.subFolders)) {
            entity.subFolders = repo.load(data.subFolders);
            for (var subFolder : entity.subFolders)
                subFolder.parent = entity;
        }

        if (!Utils.isEmpty(data.files)) {
            entity.files = fileRepo.load(data.files);
            for (var file : entity.files)
                file.parent = entity;
        }

        if (!Utils.isEmpty(data.name))
            entity.name = data.name;
        if (data.ownerId != null)
            entity.ownerId = data.ownerId;
        if (data.parent != null && data.parent.id != null)
            entity.parent = repo.findById(data.parent.id);

        repo.persist(entity);
        return response.build(200, null, entity);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Delete a folder")
    public Response delete (
        @PathParam("id")
        @Parameter(description = "Folder id to delete") Long id) {
        UserFolder entity = repo.findById(id);
        if (entity == null)
            return response.build(404);

        if (verifyACL(entity, Long.parseLong(jwt.getName()), jwt.getGroups()))
            return response.build(403);

        repo.delete(entity);
        return response.build(200);
    }

    @GET
    @Path("/stats")
    @Operation(summary = "Some statistics")
    @RolesAllowed({ "Admin" })
    public Response stats() {
        HashMap<String, Object> ret = new HashMap<>();
        HashMap<String, Integer> folderCount = new HashMap<>();
        var stats = repo.getNewFoldersByDate();
        Integer count = (int) repo.count();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        for (var stat : stats) {
            Date date = stat.getDate();
            if (date == null) {
                date = new Date(System.currentTimeMillis());
            }
            String text = df.format(date);
            folderCount.put(text, stat.getCount());
        }
        ret.put("newFolders", folderCount);
        ret.put("count", count);
        return response.build(200, "", ret);
    }

    private Boolean verifyACL(
        UserFolder folder, Long ownerId, Set<String> groups) {
        if (groups.contains("Admin"))
            return true;
        else if (folder.ownerId.equals(ownerId))
            return true;
        else
            return false;
    }
}
