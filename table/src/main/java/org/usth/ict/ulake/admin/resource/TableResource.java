package org.usth.ict.ulake.admin.resource;

import java.util.HashMap;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.admin.persistence.TableRepository;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.service.FileService;


@Path("/table")
@Produces(MediaType.APPLICATION_JSON)
public class TableResource {
    private static final Logger log = LoggerFactory.getLogger(TableResource.class);

    @Inject
    LakeHttpResponse response;

    @Inject
    TableRepository repo;

    @Inject
    @RestClient
    FileService fileService;

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
