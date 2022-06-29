package org.usth.ict.ulake.table.resource;

import java.util.Date;
import java.util.HashMap;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.service.DashboardService;
import org.usth.ict.ulake.table.model.LogEntry;
import org.usth.ict.ulake.table.persistence.LogRepository;

import com.fasterxml.jackson.databind.ObjectMapper;


@Path("/log")
@Produces(MediaType.APPLICATION_JSON)
public class LogResource {
    private static final Logger log = LoggerFactory.getLogger(LogResource.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    LakeHttpResponse response;

    @Inject
    LogRepository repo;

    @Inject
    JsonWebToken jwt;

    @Inject
    @RestClient
    DashboardService dashboardService;

    @GET
    @Operation(summary = "List all log entries")
    @RolesAllowed({ "Admin" })
    public Response all() {
        return response.build(200, "", repo.listAll());
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "Admin" })
    @Operation(summary = "Get one log entry")
    public Response one(@PathParam("id") @Parameter(description = "Logid to search") Long id) {
        LogEntry ret = repo.findById(id);
        return response.build(200, null, ret);
    }

    @GET
    @Path("/user/{uid}")
    @RolesAllowed({ "Admin" })
    @Operation(summary = "Get log entries for a specific user")
    public Response byUser(@PathParam("uid") @Parameter(description = "User id search") Long uid) {
        var ret = repo.find("ownerId", uid).list();
        return response.build(200, null, ret);
    }

    @GET
    @Path("/user")
    @RolesAllowed({ "Admin" })
    @Operation(summary = "Get log entries for current user")
    public Response byCurrentUser() {
        Long uid = Long.parseLong(jwt.getClaim(Claims.sub));
        var ret = repo.find("ownerId", uid).list();
        return response.build(200, null, ret);
    }


    @GET
    @Path("/from/{ts1}/to/{ts2}")
    @RolesAllowed({ "Admin" })
    @Operation(summary = "Get log entries for a specific timestamp range (ts1 to ts2)")
    public Response byTime(@PathParam("ts1") @Parameter(description = "From timestamp") Long ts1,
                    @PathParam("ts2") @Parameter(description = "To timestamp") Long ts2) {
        var ret = repo.find("timestamp >= ?1 and timestamp <= ?2", ts1, ts2).list();
        return response.build(200, null, ret);
    }

    @GET
    @Path("/tag")
    @RolesAllowed({ "Admin" })
    @Operation(summary = "Get log entries for a specific tag")
    public Response byTag(@QueryParam("q") @Parameter(description = "Tag to search") String tag) {
        var ret = repo.find("tag", tag).list();
        return response.build(200, null, ret);
    }

    @GET
    @Path("/service")
    @RolesAllowed({ "Admin" })
    @Operation(summary = "Get log entries for a specific service")
    public Response byService(@QueryParam("q") @Parameter(description = "Service to search") String service) {
        var ret = repo.find("service", service).list();
        return response.build(200, null, ret);
    }

    @POST
    @Transactional
    @PermitAll
    //@RolesAllowed({ "Admin" })
    @Operation(summary = "Make a new log entry")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(@RequestBody(description = "Log entry to save") LogEntry entity) {
        if (entity == null) {
            return response.build(400, "", entity);
        }

        if ("".equals(entity.service) || entity.service == null) {
            entity.service = "Log";
        }
        if ("".equals(entity.tag) || entity.tag == null) {
            entity.service = "Log";
        }
        if (jwt.getClaim(Claims.sub) != null) {
            entity.ownerId = Long.parseLong(jwt.getClaim(Claims.sub));
        }
        else {
            entity.ownerId = 0L;
        }
        entity.timestamp = new Date().getTime();
        repo.persist(entity);
        log.info("Making a new Log Entry at {}: {}", entity.timestamp, entity.content);
        return response.build(200, "", entity);
    }

    @GET
    @Path("/stats")
    @Operation(summary = "Statistics about logs")
    @RolesAllowed({ "Admin" })
    public Response tableStats(@HeaderParam("Authorization") String bearer) {
        HashMap<String, Object> ret = new HashMap<>();
        ret.put("entries", repo.count());
        return response.build(200, "", ret);
    }
}
