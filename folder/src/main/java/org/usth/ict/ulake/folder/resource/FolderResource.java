package org.usth.ict.ulake.folder.resource;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
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
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.usth.ict.ulake.common.misc.AclUtil;
import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.PermissionModel;
import org.usth.ict.ulake.common.model.log.LogModel;
import org.usth.ict.ulake.common.service.AclService;
import org.usth.ict.ulake.common.service.LogService;
import org.usth.ict.ulake.folder.model.UserFolder;
import org.usth.ict.ulake.folder.persistence.FileRepository;
import org.usth.ict.ulake.folder.persistence.FolderRepository;

@Path("/folder")
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

    @Inject
    @RestClient
    AclService aclSvc;

    @Inject
    @RestClient
    LogService logService;

    @GET
    @RolesAllowed({ "Admin" })
    @Operation(summary = "List all folders")
    public Response all(@HeaderParam("Authorization") String bearer) {
        logService.post(bearer, new LogModel("Query", "Get all folders"));
        return response.build(200, "", repo.listAll());
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Get one folder")
    public Response one(
        @HeaderParam("Authorization") String bearer,
        @PathParam("id")
        @Parameter(description = "Folder id to search") Long id) {
        var permit = PermissionModel.READ; // <-- permit
        var folder = repo.findById(id);

        if (folder == null)
            return response.build(404, "Folder not found");

        if (!AclUtil.verifyFolderAcl(
                    aclSvc, jwt, folder.id, folder.ownerId, permit))
            return response.build(403);
        logService.post(bearer, new LogModel("Query", "Get folder info for id " + id));
        return response.build(200, null, folder);
    }

    /**
     * Provide root folders and files in a virtual folder
     * */
    @GET
    @Path("/root")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "List root folder")
    public Response root(@HeaderParam("Authorization") String bearer) {
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
        logService.post(bearer, new LogModel("Query", "Get root folder info"));
        return response.build(200, null, root);
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Create a new folder")
    public Response post(
        @HeaderParam("Authorization") String bearer,
        @RequestBody(description = "Folder to save") UserFolder entity) {
        var permit = PermissionModel.WRITE;     // <-- permit
        var parentPermit = PermissionModel.ADD; // <-- permit

        if (!AclUtil.verifyFolderAcl(aclSvc, jwt, null, entity.ownerId, permit))
            return response.build(403, "Create folder not allowed");

        if (entity.parent != null && entity.parent.id != null) {
            var parent = repo.findById(entity.parent.id);
            if (parent == null)
                return response.build(403, "Parent folder is not existed");

            if (!AclUtil.verifyFolderAcl(
                        aclSvc, jwt, parent.id, parent.ownerId, parentPermit))
                return response.build(403, "Add folder not allowed");
            entity.parent = parent;
        }

        repo.persist(entity);
        logService.post(bearer, new LogModel("Add", "Create folder info for id " + entity.id + ", name " + entity.name));
        return response.build(200, "", entity);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Update a folder information")
    public Response update(
        @HeaderParam("Authorization") String bearer,
        @PathParam("id")
        @Parameter(description = "Folder id to update")
        Long id,
        @RequestBody(description = "New folder information")
        UserFolder data) {
        var permit = PermissionModel.WRITE; // <-- permit
        var parentPermit = PermissionModel.ADD; // <-- permit

        UserFolder entity = repo.findById(id);

        if (entity == null)
            return response.build(404, "Folder not found");

        if (!AclUtil.verifyFolderAcl(
                    aclSvc, jwt, entity.id, entity.ownerId, permit))
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

        if (data.parent != null && data.parent.id != null) {
            var parent = repo.findById(data.parent.id);
            if (parent == null)
                return response.build(403, "Parent folder is not existed");

            if (!AclUtil.verifyFolderAcl(
                        aclSvc, jwt, parent.id, parent.ownerId, parentPermit))
                return response.build(403, "Move file not allowed");
            entity.parent = parent;
        }

        if (!Utils.isEmpty(data.name))
            entity.name = data.name;
        if (data.ownerId != null)
            entity.ownerId = data.ownerId;

        repo.persist(entity);
        logService.post(bearer, new LogModel("Update", "Update folder info for id " + id));
        return response.build(200, null, entity);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Delete a folder")
    public Response delete (
        @HeaderParam("Authorization") String bearer,
        @PathParam("id")
        @Parameter(description = "Folder id to delete") Long id) {
        var permit = PermissionModel.WRITE; // <-- permit
        UserFolder entity = repo.findById(id);
        if (entity == null)
            return response.build(404);

        if (!AclUtil.verifyFolderAcl(
                    aclSvc, jwt, entity.id, entity.ownerId, permit))
            return response.build(403);

        repo.delete(entity);
        logService.post(bearer, new LogModel("Delete", "Delete file info for id " + id));
        return response.build(200);
    }

    @GET
    @Path("/stats")
    @Operation(summary = "Some statistics")
    @RolesAllowed({ "Admin" })
    public Response stats(@HeaderParam("Authorization") String bearer) {
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
        logService.post(bearer, new LogModel("Query", "Get folder statistics"));
        return response.build(200, "", ret);
    }
}
