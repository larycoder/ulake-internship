package org.usth.ict.ulake.dashboard.resource;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
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
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.core.GroupObjectModel;
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
    JsonWebToken jwt;

    @GET
    @RolesAllowed({"User", "Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "list all folder of user")
    public Response folder(
        @QueryParam("filter") List<String> filterStr) {
        String bearer = "bearer " + jwt.getRawToken();

        // collect filters
        var filters = new ArrayList<FilterModel>();
        for (String f : filterStr) {
            filters.add(new FilterModel(f));
        }

        // filter data
        var folderResp = fileSvc.folderList(bearer);
        var type = new TypeReference<List<FolderModel>>() {};
        var folders = mapper.convertValue(folderResp.getResp(), type);
        var filterSvc = new FilterServiceImpl<FolderModel>();

        try {
            for (FilterModel filter : filters) {
                folders = filterSvc.filter(folders, filter);
            }
        } catch (QueryException e) {
            resp.build(400, e.toString());
        }

        return resp.build(200, null, folders);
    }

    @GET
    @Path("/{folderId}")
    @RolesAllowed({"User", "Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "get folder detail")
    public Response folderInfo(
        @PathParam("folderId") Long folderId) {
        String bearer = "bearer " + jwt.getRawToken();
        var folder = fileSvc.folderInfo(bearer, folderId).getResp();
        return resp.build(200, null, folder);
    }

    @POST
    @RolesAllowed({"User", "Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "create new folder")
    public Response post(
        @RequestBody(description = "Group information")
        FolderModel folder) {
        String bearer = "bearer " + jwt.getRawToken();
        folder.ownerId = Long.parseLong(jwt.getClaim(Claims.sub));

        var objGroup = new GroupObjectModel();
        objGroup.name = folder.name;

        var objGroupResp = coreSvc.newGroup(objGroup, bearer);
        objGroup = mapper.convertValue(
                       objGroupResp.getResp(), GroupObjectModel.class);

        folder.coreGroupId = objGroup.id;
        var folderResp = fileSvc.newFolder(bearer, folder);
        return resp.build(200, null, folderResp.getResp());
    }

    @GET
    @Path("/{id}/group")
    @RolesAllowed({"User", "Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "get group information of folder")
    public Response list(@PathParam("id") Long id) {
        String bearer = "Bearer " + jwt.getRawToken();

        var folderResp = fileSvc.folderInfo(bearer, id);
        var folder = mapper.convertValue(
                         folderResp.getResp(), FolderModel.class);

        var groupResp = coreSvc.groupInfo(folder.coreGroupId, bearer);
        var group = mapper.convertValue(
                        groupResp.getResp(), GroupObjectModel.class);

        return resp.build(200, null, group);
    }

    @PUT
    @Path("/{id}/group")
    @RolesAllowed({"User", "Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "update group of folder")
    public Response add(
        @PathParam("id") Long id,
        @RequestBody  GroupObjectModel group) {
        String bearer = "Bearer " + jwt.getRawToken();

        var folderResp = fileSvc.folderInfo(bearer, id);
        var folder = mapper.convertValue(
                         folderResp.getResp(), FolderModel.class);

        var updateResp = coreSvc.updateGroup(folder.coreGroupId, group, bearer);
        return resp.build(200, null, updateResp.getResp());
    }
}
