package org.usth.ict.ulake.dashboard.resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.dashboard.FolderEntry;
import org.usth.ict.ulake.common.model.dashboard.FolderInfo;
import org.usth.ict.ulake.common.model.folder.FolderModel;
import org.usth.ict.ulake.common.service.CoreService;
import org.usth.ict.ulake.common.service.FileService;
import org.usth.ict.ulake.dashboard.filter.FilterModel;
import org.usth.ict.ulake.dashboard.filter.QueryException;
import org.usth.ict.ulake.dashboard.filter.impl.FilterServiceImpl;

@Path("/folder")
@Tag(name = "Folder")
public class FolderResource {
    private static final Logger log = LoggerFactory.getLogger(FolderResource.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    @RestClient
    FileService fileSvc;

    @Inject
    @RestClient
    CoreService coreSvc;

    @Inject
    LakeHttpResponse resp;

    @Inject
    LakeHttpResponse<FolderModel> respFolder;

    @Inject
    JsonWebToken jwt;

    private <T> List<T> filter(List<T> data, List<String> filterStr)
    throws QueryException {
        // collect filters
        var filters = new ArrayList<FilterModel>();
        for (String f : filterStr) {
            filters.add(new FilterModel(f));
        }
        // filter data
        var filterSvc = new FilterServiceImpl<T>();
        for (FilterModel filter : filters) {
            data = filterSvc.filter(data, filter);
        }
        return data;
    }

    @GET
    @Path("/root")
    @RolesAllowed({"User", "Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "list all root folder of user")
    public Response root(
        @QueryParam("filter") List<String> filterStr) {
        String bearer = "bearer " + jwt.getRawToken();

        // data
        var folderResp = fileSvc.rootInfo(bearer);
        var folder = mapper.convertValue(
                         folderResp.getResp(), FolderEntry.class);

        try {
            folder.subFolders = filter(folder.subFolders, filterStr);
            folder.files = filter(folder.files, filterStr);
        } catch (QueryException e) {
            return resp.build(400, e.toString());
        }
        return resp.build(200, null, folder);
    }

    @GET
    @RolesAllowed({"User", "Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "list all user folder of user")
    public Response list(
        @QueryParam("filter") List<String> filterStr) {
        String bearer = "bearer " + jwt.getRawToken();

        // data
        var folderResp = fileSvc.folderList(bearer);
        var type = new TypeReference<List<FolderInfo>>() {};
        var folders = mapper.convertValue(folderResp.getResp(), type);

        try {
            folders = filter(folders, filterStr);
        } catch (QueryException e) {
            return resp.build(400, e.toString());
        }
        return resp.build(200, null, folders);
    }

    @GET
    @Path("/{folderId}")
    @RolesAllowed({"User", "Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "get folder detail")
    public Response info(
        @PathParam("folderId") Long folderId) {
        String bearer = "bearer " + jwt.getRawToken();
        var folder = fileSvc.folderInfo(bearer, folderId).getResp();
        var folderInfo = mapper.convertValue(folder, FolderInfo.class);
        return resp.build(200, null, folderInfo);
    }

    @GET
    @Path("/{folderId}/entries")
    @RolesAllowed({"User", "Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "list of folder entries")
    public Response subList(
        @PathParam("folderId") Long folderId,
        @QueryParam("filter") List<String> filterStr) {
        String bearer = "bearer " + jwt.getRawToken();
        var folderResp = fileSvc.folderInfo(bearer, folderId).getResp();
        var folder = mapper.convertValue(folderResp, FolderEntry.class);

        try {
            folder.subFolders = filter(folder.subFolders, filterStr);
            folder.files = filter(folder.files, filterStr);
        } catch (QueryException e) {
            return resp.build(400, e.toString());
        }
        return resp.build(200, null, folder);
    }

    @PUT
    @Path("/{folderId}")
    @RolesAllowed({"User", "Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "update folder info")
    public Response update(
        @PathParam("folderId") Long folderId,
        @RequestBody FolderInfo data) {
        String bearer = "bearer " + jwt.getRawToken();
        data.id = folderId;
        var folder = mapper.convertValue(data, FolderModel.class);
        var update = fileSvc.updateFolder(bearer, folderId, folder).getResp();
        return resp.build(200, null, update);
    }

    @POST
    @RolesAllowed({"User", "Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "create new folder")
    public Response post(
        @RequestBody(description = "Group information")
        FolderModel folder) {
        String bearer = "bearer " + jwt.getRawToken();
        Long jwtUserId = Long.parseLong(jwt.getClaim(Claims.sub));
        if (folder.ownerId != null && folder.ownerId != 0 && jwt.getGroups().contains("Admin")) {
            log.warn("Manually setting owner id {} from admin {}", folder.ownerId, jwtUserId);
        }
        else {
            folder.ownerId = jwtUserId;
        }

        folder.creationTime = new Date().getTime();
        var folderResp = fileSvc.newFolder(bearer, folder);
        return resp.build(200, null, folderResp.getResp());
    }

    @DELETE
    @Path("/{folderId}")
    @RolesAllowed({"User", "Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete a folder")
    public Response delete(@HeaderParam("Authorization") String bearer,
        @PathParam("folderId") @Parameter(description = "Folder id to delete") Long id) {
        var folder = fileSvc.delFolder(bearer, id).getResp();
        return respFolder.build(200, null, folder);
    }

    private FolderModel getFolderEntry(String bearer, Long folderId) {
        log.info("Looking for folder {} from folder service", folderId);
        return fileSvc.folderInfo(bearer, folderId).getResp();
    }

    private boolean deleteFolderRecursively(String bearer, FolderModel parent) {
        boolean ret = true;
        for (var file: parent.files) {
            var delFile = fileSvc.deleteFile(bearer, file.id).getResp();
            if (delFile != null && file.id != delFile.id) {
                log.error("Cannot delete file {}", file.id);
                ret = false;
            }
        }
        for (var folder: parent.subFolders) {
            ret |= deleteFolderRecursively(bearer, folder);
        }
        var delFolder = fileSvc.delFolder(bearer, parent.id).getResp();
        if (delFolder != null && delFolder.id != parent.id) {
            log.error("Cannot delete folder {}", parent.id);
            ret = false;
        }
        return ret;
    }

    @DELETE
    @Path("/{folderId}/recursive")
    @RolesAllowed({"User", "Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete a folder, recursively!!!")
    public Response deleteRecursive(@HeaderParam("Authorization") String bearer,
        @PathParam("folderId") @Parameter(description = "Folder id to delete") Long id) {

        var folder = getFolderEntry(bearer, id);
        var ret = deleteFolderRecursively(bearer, folder);
        if (ret)
            return respFolder.build(200);
        else
            return respFolder.build(403);
    }
}
