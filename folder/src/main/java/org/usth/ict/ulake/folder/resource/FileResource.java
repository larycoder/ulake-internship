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
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.usth.ict.ulake.common.misc.AclUtil;
import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.PermissionModel;
import org.usth.ict.ulake.common.service.AclService;
import org.usth.ict.ulake.folder.model.UserFile;
import org.usth.ict.ulake.folder.model.UserFileSearchQuery;
import org.usth.ict.ulake.folder.persistence.FileRepository;
import org.usth.ict.ulake.folder.persistence.FolderRepository;

@Path("/file")
@Produces(MediaType.APPLICATION_JSON)
public class FileResource {
    @Inject
    FileRepository repo;

    @Inject
    FolderRepository folderRepo;

    @Inject
    LakeHttpResponse response;

    @Inject
    JsonWebToken jwt;

    @Inject
    @RestClient
    AclService aclSvc;

    @GET
    @RolesAllowed({ "Admin" })
    @Operation(summary = "List all files")
    public Response all() {
        return response.build(200, "", repo.listAll());
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Get a single file info")
    public Response one(
        @PathParam("id")
        @Parameter(description = "File id to search") Long id) {
        var permit = PermissionModel.READ; // <-- permit
        var file = repo.findById(id);

        if (file == null)
            return response.build(404, "File not found");

        if (!AclUtil.verifyACL(aclSvc, jwt, file.id, file.ownerId, permit))
            return response.build(403);

        return response.build(200, null, file);
    }

    @POST
    @Path("/search")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Search for files")
    public Response search(
        @RequestBody(description = "Query to perform search for user files")
        UserFileSearchQuery query) {
        // TODO: admin verification
        var results = repo.search(query);
        if (results.isEmpty()) {
            return response.build(404);
        }
        return response.build(200, null, results);
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a new file info")
    @RolesAllowed({ "User", "Admin" })
    public Response post(UserFile entity) {
        // TODO: missing appropriate permission mechanism

        repo.persist(entity);
        return response.build(200, null, entity);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Update a file info")
    public Response update(
        @PathParam("id")
        @Parameter(description = "File id to update") Long id,
        @RequestBody(description = "New file info to save") UserFile data) {
        var permit = PermissionModel.WRITE; // <-- permit
        UserFile file = repo.findById(id);

        if (file == null)
            return response.build(404, "File not found");

        if (!AclUtil.verifyACL(aclSvc, jwt, file.id, file.ownerId, permit))
            return response.build(403);

        if (!Utils.isEmpty(data.cid) && data.size != null) {
            file.cid = data.cid;
            file.size = data.size;
        }

        if (!Utils.isEmpty(data.mime)) file.mime = data.mime;
        if (!Utils.isEmpty(data.name)) file.name = data.name;
        if (data.ownerId != null) file.ownerId = data.ownerId;

        if (data.parent != null && data.parent.id != null) {
            file.parent = folderRepo.findById(data.parent.id);
        }

        repo.persist(file);
        return response.build(200, null, file);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Delete a file")
    public Response delete (
        @PathParam("id")
        @Parameter(description = "File id to delete")
        Long id) {
        var permit = PermissionModel.WRITE; // <-- permit
        UserFile entity = repo.findById(id);
        if (entity == null)
            return response.build(404);

        if (!AclUtil.verifyACL(aclSvc, jwt, entity.id, entity.ownerId, permit))
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
        var stats = repo.getNewFilesByDate();
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
        ret.put("newFiles", folderCount);
        ret.put("count", count);
        return response.build(200, "", ret);
    }
}
