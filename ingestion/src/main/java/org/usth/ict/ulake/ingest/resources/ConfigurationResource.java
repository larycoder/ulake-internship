package org.usth.ict.ulake.ingest.resources;

import java.util.Date;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.ingest.model.UserConfigure;
import org.usth.ict.ulake.ingest.persistence.UserConfigureRepo;

@Path("/configure")
public class ConfigurationResource {
    @Inject
    LakeHttpResponse resp;

    @Inject
    UserConfigureRepo repo;

    @Inject
    ObjectMapper mapper;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "list all configuration")
    public Response list() {
        return resp.build(200, "", repo.listAll());
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "get configure by id")
    public Response one(@PathParam("id") Long id) {
        UserConfigure conf = repo.findById(id);
        if (conf == null)
            return resp.build(404, "Configuration not found");
        return resp.build(200, "", conf);
    }

    @GET
    @Path("/{id}/query")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "get query by configure id")
    public Response configure(@PathParam("id") Long id) {
        UserConfigure conf = repo.findById(id);
        if (conf == null)
            return resp.build(404, "Configuration not found");
        return resp.build(200, "", conf.query);
    }

    @POST
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "add new configuration")
    public Response post(UserConfigure conf) {
        conf.createdTime = conf.updatedTime = new Date().getTime();
        repo.persist(conf);
        return resp.build(200, "", conf);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "update a configuration")
    public Response update(
        @PathParam("id") Long id,
        @RequestBody UserConfigure entity) {
        UserConfigure conf = repo.findById(id);
        if (conf == null)
            return resp.build(404, "Configuration not found");

        if (entity.query != null) conf.query = entity.query;
        if (entity.ownerId != null) conf.ownerId = entity.ownerId;

        if (!Utils.isEmpty(entity.description))
            conf.description = entity.description;

        conf.updatedTime = new Date().getTime();

        repo.persist(conf);
        return resp.build(200, "", conf);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "delete a configuration")
    public Response delete (@PathParam("id") Long id) {
        UserConfigure conf = repo.findById(id);
        if (conf == null)
            return resp.build(404, "Configuration not found");
        if (repo.deleteById(id))
            return resp.build(200, "", conf);
        else
            return resp.build(500, "Fail to delete configuration", conf);
    }
}
