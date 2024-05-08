package org.usth.ict.ulake.admin.resource;

import java.util.HashMap;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.service.FileService;


@Path("/admin/files")
@Produces(MediaType.APPLICATION_JSON)
public class AdminFileResource {
    private static final Logger log = LoggerFactory.getLogger(AdminUserResource.class);

    @Inject
    LakeHttpResponse response;

    @Inject
    @RestClient
    FileService fileService;

    @GET
    @Path("/stats")
    @Operation(summary = "Statistics about files")
    @RolesAllowed({ "User", "Admin" })
    public Response userStats(@HeaderParam("Authorization") String bearer) {
        // get requests from other service
        HashMap<String, Object> ret = new HashMap<>();

        // folder stats
        var stats = fileService.fileStats(bearer);
        HashMap<String, String> fileStats = (HashMap<String, String>) stats.getResp();
        return response.build(200, "", fileStats);
    }
}
