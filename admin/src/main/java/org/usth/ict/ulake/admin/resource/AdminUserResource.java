package org.usth.ict.ulake.admin.resource;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Date;
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
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.service.UserService;


@Path("/admin/users")
@Produces(MediaType.APPLICATION_JSON)
public class AdminUserResource {
    private static final Logger log = LoggerFactory.getLogger(AdminUserResource.class);

    @Inject
    LakeHttpResponse response;

    @Inject
    @RestClient
    UserService userService;

    @GET
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "Nothing yet.")
    public Response all() {
        // TODO: list all users from user.
        return response.build(200, null, new Date());
    }

    @GET
    @Path("/stats")
    @Operation(summary = "Statistics about users")
    @RolesAllowed({ "User", "Admin" })
    public Response userStats(@HeaderParam("Authorization") String bearer) {
        // get requests from other service
        //HashMap<String, Object> ret = new HashMap<>();

        // system uptime
        // RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        // long uptime = bean.getUptime();
        // ret.put("uptime", uptime);
        // ret.put("regs", regs);

        // user registration
        var userStats = userService.getStats(bearer);
        HashMap<String, Integer> regs = (HashMap<String, Integer>) userStats.getResp();
        return response.build(200, "", regs);
    }
}
