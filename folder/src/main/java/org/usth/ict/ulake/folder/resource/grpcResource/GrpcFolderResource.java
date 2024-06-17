package org.usth.ict.ulake.folder.resource.grpcResource;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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

import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
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
import org.usth.ict.ulake.common.model.PermissionModel;
import org.usth.ict.ulake.common.model.acl.macro.FileType;
import org.usth.ict.ulake.common.model.folder.UserFolderSearchQuery;
import org.usth.ict.ulake.common.model.log.LogModel;
import org.usth.ict.ulake.common.service.LogService;
import org.usth.ict.ulake.folder.model.UserFolder;
import org.usth.ict.ulake.folder.persistence.FileRepository;
import org.usth.ict.ulake.folder.persistence.FolderRepository;

@Path("/folder")
@Produces(MediaType.APPLICATION_JSON)
public class GrpcFolderResource {
    private static final Logger log = LoggerFactory.getLogger(GrpcFolderResource.class);

    @Inject
    JsonWebToken jwt;

    @Inject
    FolderRepository repo;

    @Inject
    FileRepository fileRepo;

    @Inject
    LakeHttpResponse<UserFolder> resp;

    @Inject
    LakeHttpResponse<Object> respObject;

    @Inject
    GrpcAclUtil acl;

    @Inject
    @RestClient
    LogService logService;

    @GET
    @RolesAllowed({ "Admin" })
    @Operation(summary = "List all folders")
    public Response all(@HeaderParam("Authorization") String bearer) {
        logService.post(bearer, new LogModel("Query", "Get all folders"));
        return resp.build(200, "", repo.listAll());
    }

    /**
     * Provide root folders and files in a virtual folder
     * */
    @GET
    @Path("/root/{uid}")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "List root folder")
    public Response root(@HeaderParam("Authorization") String bearer,
                         @PathParam("uid") @Parameter(description = "User id to list root dir")  Long uid) {
        var ownerId = Long.parseLong(jwt.getClaim(Claims.sub));
        UserFolder root = new UserFolder();
        root.ownerId = ownerId;
        if (jwt.getGroups().contains("Admin")) {
            if (uid == null) {
                // all files and folders of all users
                root.subFolders = repo.listRoot();
                root.files = fileRepo.listRoot();
            } else {
                root.subFolders = repo.listRoot(uid);
                root.files = fileRepo.listRoot(uid);
            }
        } else {
            root.subFolders = repo.listRoot(ownerId);
            root.files = fileRepo.listRoot(ownerId);
        }

        // don't go too deep.
        if (root.subFolders != null) {
            for (var subFolder : root.subFolders) {
                subFolder.subFolders = null;
                subFolder.files = null;
            }
        }
        logService.post(bearer, new LogModel("Query", "Get root folder info"));
        return resp.build(200, null, root);
    }

    /**
     * Provide root folders and files in a virtual folder
     * */
    @GET
    @Path("/root")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "List root folder of current user")
    public Response root(@HeaderParam("Authorization") String bearer) {
        return root(bearer, null);
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Create a new folder")
    public Response post(
            @HeaderParam("Authorization") String bearer,
            @RequestBody(description = "Folder to save") UserFolder entity) {
        var permit = io.quarkus.example.PermissionModel.WRITE;     // <-- permit
        var parentPermit = io.quarkus.example.PermissionModel.ADD; // <-- permit

        if (!acl.verify(io.quarkus.example.FileType.FILE, null, entity.ownerId, permit))
            return resp.build(403, "Create folder not allowed");

        if (entity.parent != null && entity.parent.id != null) {
            var parent = repo.findById(entity.parent.id);
            if (parent == null)
                return resp.build(403, "Parent folder is not existed");

            if (!acl.verify(io.quarkus.example.FileType.FOLDER, parent.id, parent.ownerId, parentPermit))
                return resp.build(403, "Add folder not allowed");
            entity.parent = parent;
        }

        Long jwtUserId = Long.parseLong(jwt.getClaim(Claims.sub));
        if (entity.ownerId != null && entity.ownerId != 0 && jwt.getGroups().contains("Admin")) {
            log.warn("Manually setting owner id {} from admin {}", entity.ownerId, jwtUserId);
        } else {
            entity.ownerId = jwtUserId;
        }

        repo.persist(entity);
        logService.post(bearer, new LogModel("Add", "Create folder info for id " + entity.id + ", name " + entity.name));
        return resp.build(200, "", entity);
    }

}
