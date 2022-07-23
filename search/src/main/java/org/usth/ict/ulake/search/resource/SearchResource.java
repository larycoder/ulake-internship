package org.usth.ict.ulake.search.resource;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.folder.FileModel;
import org.usth.ict.ulake.common.model.folder.UserFileSearchQuery;
import org.usth.ict.ulake.common.model.search.FilterModel;
import org.usth.ict.ulake.common.model.user.User;
import org.usth.ict.ulake.common.model.user.UserSearchQuery;
import org.usth.ict.ulake.common.service.FileService;
import org.usth.ict.ulake.common.service.UserService;
import org.usth.ict.ulake.common.service.exception.LakeServiceException;
import org.usth.ict.ulake.common.service.exception.LakeServiceNotFoundException;

@Path("/search")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SearchResource {
    private static final Logger log = LoggerFactory.getLogger(SearchResource.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    LakeHttpResponse<Object> response;

    @Inject
    @RestClient
    UserService userSvc;

    @Inject
    @RestClient
    FileService fileSvc;

    private List<User> searchUser(String bearer, UserSearchQuery query) {
        try {
            return userSvc.search(bearer, query).getResp();
        } catch (LakeServiceNotFoundException e) {
            log.error("Not found error ({})", userSvc, e);
            return new ArrayList<User>();
        } catch (LakeServiceException e) {
            log.error("Unknown error ({})", userSvc, e);
            return null;
        } catch (IllegalArgumentException e) {
            log.error("Fail to parse data ({})", userSvc, e);
            return null;
        }
    }

    private List<FileModel> searchFile(String bearer, UserFileSearchQuery query) {
        try {
            return fileSvc.search(bearer, query).getResp();
        } catch (LakeServiceNotFoundException e) {
            log.error("Not found error ({})", fileSvc, e);
            return new ArrayList<FileModel>();
        } catch (LakeServiceException e) {
            log.error("Unknown error ({})", fileSvc, e);
            return null;
        } catch (IllegalArgumentException e) {
            log.error("Fail to parse data ({})", fileSvc, e);
            return null;
        }
    }

    @POST
    @Path("/user")
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "Search user information")
    public Response user(
        @HeaderParam("Authorization") String bearer, FilterModel filter) {
        if (filter.userQuery == null)
            filter.userQuery = new UserSearchQuery();

        // file query
        if (filter.fileQuery != null) {
            if (filter.userQuery.ids == null)
                filter.userQuery.ids = new ArrayList<Long>();

            var files = searchFile(bearer, filter.fileQuery);
            if (files != null && !files.isEmpty()) {
                for (var file : files) {
                    if (file.ownerId != null &&
                            !filter.userQuery.ids.contains(file.ownerId)) {
                        filter.userQuery.ids.add(file.ownerId);
                    }
                }
            }
        }

        // main user query
        var users = searchUser(bearer, filter.userQuery);
        if (users == null) {
            return response.build(500, "internal error");
        } else if (users.isEmpty()) {
            return response.build(404, "not found error");
        } else {
            return response.build(200, null, users);
        }
    }

    @POST
    @Path("/file")
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "Search file information")
    public Response file(
        @HeaderParam("Authorization") String bearer, FilterModel filter) {
        if (filter.fileQuery == null)
            filter.fileQuery = new UserFileSearchQuery();

        // user query
        if (filter.userQuery != null) {
            if (filter.fileQuery.ownerIds == null)
                filter.fileQuery.ownerIds = new ArrayList<Long>();

            var users = searchUser(bearer, filter.userQuery);
            if (users != null && !users.isEmpty()) {
                for (var user : users) {
                    if (user.id != null &&
                            !filter.fileQuery.ownerIds.contains(user.id)) {
                        filter.fileQuery.ownerIds.add(user.id);
                    }
                }
            }
        }

        // main file query
        var files = searchFile(bearer, filter.fileQuery);
        if (files == null) {
            return response.build(500, "internal error");
        } else if (files.isEmpty()) {
            return response.build(404, "not found error");
        } else {
            return response.build(200, null, files);
        }
    }
}
