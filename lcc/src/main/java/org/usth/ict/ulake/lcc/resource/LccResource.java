package org.usth.ict.ulake.lcc.resource;

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
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.log.LogModel;
import org.usth.ict.ulake.common.service.LogService;
import org.usth.ict.ulake.lcc.persistence.PatientRepository;

@Path("/file")
@Produces(MediaType.APPLICATION_JSON)
public class LccResource {
    private static final Logger log = LoggerFactory.getLogger(LccResource.class);

    @Inject
    PatientRepository repo;

    @Inject
    LakeHttpResponse<Object> resp;

    @Inject
    @RestClient
    LogService logService;

    @GET
    @RolesAllowed({ "Admin" })
    @Operation(summary = "List all patients")
    public Response all(@HeaderParam("Authorization") String bearer) {
        logService.post(bearer, new LogModel("Query", "Get all patients"));
        return resp.build(200, "", repo.listAll());
    }
}
