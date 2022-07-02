package org.usth.ict.ulake.compress.resource;

import java.util.HashMap;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.compress.model.Request;
import org.usth.ict.ulake.compress.persistence.RequestRepository;
import org.usth.ict.ulake.compress.persistence.ResultRepository;

import com.fasterxml.jackson.databind.ObjectMapper;


@Path("/compress")
@Produces(MediaType.APPLICATION_JSON)
public class CompressResource {
    private static final Logger log = LoggerFactory.getLogger(CompressResource.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    LakeHttpResponse response;

    @Inject
    RequestRepository repoReq;

    @Inject
    RequestRepository repoReqFile;

    @Inject
    ResultRepository repoResp;

    @Inject
    JsonWebToken jwt;

    @GET
    @Operation(summary = "List all compression requests. Admin: all possible requests, User: requests of his own.")
    @RolesAllowed({ "User", "Admin" })
    public Response all() {
        Set<String> groups = jwt.getGroups();
        if (groups.contains("Admin")) {
            return response.build(200, "", repoReq.listAll());
        }
        Long userId = Long.parseLong(jwt.getClaim(Claims.sub));
        return response.build(200, "", repoReq.list("userId", userId));
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Get one request info")
    public Response one(@PathParam("id") @Parameter(description = "Request id to search") Long id) {
        Request table = repoReq.findById(id);
        return response.build(200, null, table);
    }

    @GET
    @Path("/stats")
    @Operation(summary = "Statistics about tabular datas")
    @RolesAllowed({ "User", "Admin" })
    public Response tableStats(@HeaderParam("Authorization") String bearer) {
        // get requests from other service
        HashMap<String, Object> ret = new HashMap<>();
        return response.build(200, "", ret);
    }
}
