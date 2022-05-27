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
import org.usth.ict.ulake.admin.persistence.AdminRepository;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.service.UserService;


@Path("/admin/users")
@Produces(MediaType.APPLICATION_JSON)
public class AdminUserResource {
    @Inject
    LakeHttpResponse response;

    @Inject
    AdminRepository repo;

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
        HashMap<String, Object> ret = new HashMap<>();

        RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        long uptime = bean.getUptime();
        ret.put("uptime", uptime);

        var info = userService.getStats(bearer);
        
        HashMap<String, Integer> regs = (HashMap<String, Integer>) info.getResp();
        for (String day: regs.keySet())  {
            regs.put(day, regs.get(day));
        }
        ret.put("regs", regs);
        return response.build(200, "", ret);
    }
}
