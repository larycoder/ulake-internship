package org.usth.ict.ulake.search.resource;

import java.util.List;
import java.util.ArrayList;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.client.exception.ResteasyWebApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.search.model.FilterModel;
import org.usth.ict.ulake.search.service.SearchService;
import org.usth.ict.ulake.search.service.ext.UserService;
import org.usth.ict.ulake.user.model.User;

@Path("/search")
@Produces(MediaType.APPLICATION_JSON)
public class SearchResource {
    private static final Logger log = LoggerFactory.getLogger(SearchResource.class);

    @Inject
    LakeHttpResponse response;

    @Inject
    JsonWebToken jwt;

    @Inject
    @RestClient
    UserService userSvc;

    /**
     * Generic function to query data from sub-services
     * @param <R> response type
     * @param <Q> query type
     * @param svc SearchService implementation
     * @param query query model
     * @return list of response data if success else null
     */
    @SuppressWarnings("unchecked")
    private <R, Q> List<R> queryFunc(SearchService<Q> svc, Q query) {
        try {
            String token = "bearer " + jwt.getRawToken();
            var resp = svc.search(token, query);
            return (List<R>) resp.getResp();
        } catch (ResteasyWebApplicationException e) {
            log.error("Fail to query {}", svc, e);
            return null;
        }
    }

    @POST
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "Search user information")
    public Response user(FilterModel filter) {
        List<User> users = queryFunc(userSvc, filter.userQuery);
        return response.build(200, null, users);
    }
}
