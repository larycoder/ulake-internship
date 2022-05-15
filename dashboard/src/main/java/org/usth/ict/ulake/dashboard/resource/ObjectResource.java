package org.usth.ict.ulake.dashboard.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.usth.ict.ulake.dashboard.extension.CoreService;
import org.usth.ict.ulake.dashboard.filter.FilterModel;
import org.usth.ict.ulake.dashboard.filter.QueryException;
import org.usth.ict.ulake.dashboard.filter.impl.FilterServiceImpl;
import org.usth.ict.ulake.dashboard.model.ObjectModel;
import org.usth.ict.ulake.dashboard.model.extension.ExtensionModel;

@Path("/object")
@Tag(name = "Object")
public class ObjectResource {
    @Inject
    JsonWebToken jwt;

    @Inject
    @RestClient
    CoreService coreSvc;

    @GET
    @RolesAllowed({"User", "Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "list objects of user")
    public ExtensionModel<List<ObjectModel>> object(
        @QueryParam("filter") List<String> filterStr) {
        String bearer = "bearer " + jwt.getRawToken();

        // collect filters
        var filters = new ArrayList<FilterModel>();
        for (String f : filterStr) {
            filters.add(new FilterModel(f));
        }

        // apply filters
        var filterSvc = new FilterServiceImpl<ObjectModel>();
        var objects =  coreSvc.getListObject(bearer);
        if (objects.getCode() == 200) {
            try {
                for (FilterModel filter : filters) {
                    objects.setResp(filterSvc.filter(objects.getResp(), filter));
                }
            } catch (QueryException e) {
                objects.setCode(400);
                objects.setMsg(e.toString());
                objects.setResp(null);
            }
        }
        return objects;
    }

    @GET
    @Path("/{cid}/data")
    @RolesAllowed({"User", "Admin"})
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Operation(summary = "get object data")
    public Response objectData(@PathParam("cid") String cid) {
        String bearer = "Bearer " + jwt.getRawToken();
        InputStream is = coreSvc.getObjectData(cid, bearer);
        var stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException {
                is.transferTo(os);
            }
        };
        return Response.ok(stream).build();
    }
}
