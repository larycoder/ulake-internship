package org.usth.ict.ulake.folder.resource;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import io.quarkus.example.FileType;
import io.quarkus.example.PermissionModel;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
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
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.misc.AclUtil;
import org.usth.ict.ulake.common.misc.GrpcAclUtil;
import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.folder.UserFileSearchQuery;
import org.usth.ict.ulake.common.model.folder.UserFileSearchQueryV2;
import org.usth.ict.ulake.common.model.log.LogModel;
import org.usth.ict.ulake.common.service.LogService;
import org.usth.ict.ulake.folder.model.UserFile;
import org.usth.ict.ulake.folder.persistence.FileRepository;
import org.usth.ict.ulake.folder.persistence.FolderRepository;

@Path("/file")
@Produces(MediaType.APPLICATION_JSON)
public class FileResource {
    private static final Logger log = LoggerFactory.getLogger(FileResource.class);

    @Inject
    FileRepository repo;

    @Inject
    FolderRepository folderRepo;

    @Inject
    LakeHttpResponse<UserFile> resp;

    @Inject
    LakeHttpResponse<Object> respObject;

    @Inject
    GrpcAclUtil acl;

    @Inject
    @RestClient
    LogService logService;

    @GET
    @RolesAllowed({ "Admin" })
    @Operation(summary = "List all files")
    public Response all(@HeaderParam("Authorization") String bearer) {
        logService.post(bearer, new LogModel("Query", "Get all files"));
        return resp.build(200, "", repo.listAll());
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Get one ore multiple file info")
    public Response one(
        @HeaderParam("Authorization") String bearer,
        @PathParam("id")
        @Parameter(description = "File id to search") String ids) {
        var permit = io.quarkus.example.PermissionModel.READ; // <-- permit

        log.info("getting file info for {}", ids);
        if (Utils.isNumeric(ids)) {

            Long id = Long.parseLong(ids);
            var file = repo.findById(id);

            if (file == null)
                return resp.build(404, "File not found");

            if (!acl.verify(io.quarkus.example.FileType.FILE, file.id, file.ownerId, permit))
                return resp.build(403);

            logService.post(bearer, new LogModel("Query", "Get file info for id " + id));
            return resp.build(200, null, file);
        } else {
            String idArr[] = ids.split(",");
            List<Long> idList = Arrays.asList(idArr).stream()
                                .filter(idStr -> Utils.isNumeric(idStr))
                                .mapToLong(Long::parseLong)
                                .boxed()
                                .collect(Collectors.toList());
            UserFileSearchQuery query = new UserFileSearchQuery();
            query.ids = idList;
            logService.post(bearer, new LogModel("Query", "Get many ids: " + ids));
            var files = repo.search(query);
            return resp.build(200, null, files);
        }
    }

    @POST
    @Path("/search")
    @RolesAllowed({ "Admin" })
    @Operation(summary = "Search for files")
    public Response search(
        @HeaderParam("Authorization") String bearer,
        @RequestBody(description = "Query to perform search for user files")
        UserFileSearchQuery query) {
        // TODO: allow normal user search
        var results = repo.search(query);
        if (results.isEmpty()) {
            return resp.build(404);
        }
        logService.post(bearer, new LogModel("Query", "Search file info with keyword " + query.keyword));
        return resp.build(200, null, results);
    }

    @POST
    @Path("/search/v2")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Search for files")
    public Response searchV2(
        @HeaderParam("Authorization") String bearer,
        @RequestBody(description = "Query to perform search for user files")
        UserFileSearchQueryV2 query) {
        var results = repo.searchV2(query);
        if (results.isEmpty()) {
            return resp.build(404);
        }
        logService.post(bearer, new LogModel("Query", "Search file info with keyword " + query.keywords));
        return resp.build(200, null, results);
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a new file info")
    @RolesAllowed({ "User", "Admin" })
    public Response post(@HeaderParam("Authorization") String bearer, UserFile entity) {
        var permit = io.quarkus.example.PermissionModel.WRITE;     // <-- permit
        var parentPermit = io.quarkus.example.PermissionModel.ADD; // <-- permit

//        if (!acl.verify(io.quarkus.example.FileType.FILE, null, entity.ownerId, permit))
//            return resp.build(403, "Create file not allowed");

        if (entity.parent != null && entity.parent.id != null) {
            var parent = folderRepo.findById(entity.parent.id);
            if (parent == null)
                return resp.build(403, "Parent folder is not existed");

            if (!acl.verify(io.quarkus.example.FileType.FOLDER, parent.id, parent.ownerId, parentPermit))
                return resp.build(403, "Add file not allowed");
            entity.parent = parent;
        }

        repo.persist(entity);
        logService.post(bearer, new LogModel("Add", "Create file info for id " + entity.id + ", cid " + entity.cid + ", name " + entity.name));
        return resp.build(200, null, entity);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Update a file info")
    public Response update(
        @HeaderParam("Authorization") String bearer,
        @PathParam("id")
        @Parameter(description = "File id to update") Long id,
        @RequestBody(description = "New file info to save") UserFile data) {
        var permit = io.quarkus.example.PermissionModel.WRITE;     // <-- permit
        var parentPermit = io.quarkus.example.PermissionModel.ADD; // <-- permit

        UserFile file = repo.findById(id);

        if (file == null)
            return resp.build(404, "File not found");

        if (!acl.verify(io.quarkus.example.FileType.FILE, file.id, file.ownerId, permit))
            return resp.build(403, "Update file not allowed");

        if (!Utils.isEmpty(data.cid) && data.size != null) {
            file.cid = data.cid;
            file.size = data.size;
        }

        if (!Utils.isEmpty(data.mime)) file.mime = data.mime;
        if (!Utils.isEmpty(data.name)) file.name = data.name;

        // TODO: change ownership should be verified carefully.
        if (data.ownerId != null) file.ownerId = data.ownerId;

        if (data.parent != null && data.parent.id != null) {
            var parent = folderRepo.findById(data.parent.id);
            if (parent == null)
                return resp.build(403, "Parent folder is not existed");

            if (!acl.verify(io.quarkus.example.FileType.FOLDER, parent.id, parent.ownerId, parentPermit))
                return resp.build(403, "Move file not allowed");
            file.parent = parent;
        }

        repo.persist(file);
        logService.post(bearer, new LogModel("Update", "Update file info for id " + id));
        return resp.build(200, null, file);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Delete a file")
    public Response delete (
        @HeaderParam("Authorization") String bearer,
        @PathParam("id")
        @Parameter(description = "File id to delete")
        Long id) {
        var permit = PermissionModel.WRITE; // <-- permit
        UserFile entity = repo.findById(id);
        if (entity == null)
            return resp.build(404);

        if (!acl.verify(FileType.FILE, entity.id, entity.ownerId, permit))
            return resp.build(403);

        repo.delete(entity);
        logService.post(bearer, new LogModel("Delete", "Delete file info for id " + id));
        return resp.build(200);
    }

    @GET
    @Path("/stats")
    @Operation(summary = "Some statistics")
    @RolesAllowed({ "Admin" })
    public Response stats(@HeaderParam("Authorization") String bearer) {
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
        logService.post(bearer, new LogModel("Query", "Get file statistics"));
        return respObject.build(200, "", ret);
    }
}
