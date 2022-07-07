package org.usth.ict.ulake.search.resource;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.service.exception.LakeServiceException;
import org.usth.ict.ulake.common.service.exception.LakeServiceNotFoundException;
import org.usth.ict.ulake.folder.model.UserFile;
import org.usth.ict.ulake.folder.model.UserFileSearchQuery;
import org.usth.ict.ulake.search.model.FilterModel;
import org.usth.ict.ulake.search.service.SearchService;
import org.usth.ict.ulake.search.service.ext.FolderService;
import org.usth.ict.ulake.search.service.ext.UserService;
import org.usth.ict.ulake.user.model.User;
import org.usth.ict.ulake.user.model.UserSearchQuery;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/search")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SearchResource {
    private static final Logger log = LoggerFactory.getLogger(SearchResource.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    LakeHttpResponse response;

    @Inject
    JsonWebToken jwt;

    @Inject
    @RestClient
    UserService userSvc;

    @Inject
    @RestClient
    FolderService folderSvc;

    /**
     * Generic function to query data from sub-services
     * @param <R> response type
     * @param <Q> query type
     * @param svc SearchService implementation
     * @param query query model
     * @param respType type of response
     * @return list of response data if success else null
     */
    private <R, Q> List<R> queryFunc(
        SearchService<Q> svc, Q query, Class<R> respType) {
        try {
            String token = "bearer " + jwt.getRawToken();
            var resp = svc.search(token, query);
            var type = new TypeReference<List<R>>() {};
            return mapper.convertValue(resp.getResp(), type);
        } catch (LakeServiceNotFoundException e) {
            log.error("Not found error ({})", svc, e);
            return new ArrayList<R>();
        } catch (LakeServiceException e) {
            log.error("Unknown error ({})", svc, e);
            return null;
        } catch (IllegalArgumentException e) {
            log.error("Fail to parse data ({})", svc, e);
            return null;
        }
    }

    @POST
    @Path("/user")
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "Search user information")
    public Response user(FilterModel filter) {
        if (filter.userQuery == null)
            filter.userQuery = new UserSearchQuery();

        // file query
        if (filter.fileQuery != null) {
            if (filter.userQuery.ids == null)
                filter.userQuery.ids = new ArrayList<Long>();

            var files = queryFunc(folderSvc, filter.fileQuery, UserFile.class);
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
        var users = queryFunc(userSvc, filter.userQuery, User.class);
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
    public Response file(FilterModel filter) {
        if (filter.fileQuery == null)
            filter.fileQuery = new UserFileSearchQuery();

        // user query
        if (filter.userQuery != null) {
            if (filter.fileQuery.ownerIds == null)
                filter.fileQuery.ownerIds = new ArrayList<Long>();

            var users = queryFunc(userSvc, filter.userQuery, User.class);
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
        var files = queryFunc(folderSvc, filter.fileQuery, UserFile.class);
        if (files == null) {
            return response.build(500, "internal error");
        } else if (files.isEmpty()) {
            return response.build(404, "not found error");
        } else {
            return response.build(200, null, files);
        }
    }
}
