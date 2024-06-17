package org.usth.ict.ulake.ingest.resources;

import java.util.Date;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.ingest.model.Policy;
import org.usth.ict.ulake.ingest.model.CrawlTemplate;
import org.usth.ict.ulake.ingest.persistence.CrawlTemplateRepo;

@Path("/template")
public class TemplateResource {
    @Inject
    LakeHttpResponse<CrawlTemplate> resp;

    @Inject
    LakeHttpResponse<Policy> respPolicy;

    @Inject
    CrawlTemplateRepo repo;

    @Inject
    ObjectMapper mapper;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "list all templates")
    public Response list() {
        return resp.build(200, "", repo.listAll());
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "get template by id")
    public Response one(@PathParam("id") Long id) {
        CrawlTemplate conf = repo.findById(id);
        if (conf == null)
            return resp.build(404, "template not found");
        return resp.build(200, "", conf);
    }

    @GET
    @Path("/{id}/query")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "get query by template id")
    public Response configure(@PathParam("id") Long id) {
        CrawlTemplate conf = repo.findById(id);
        if (conf == null)
            return resp.build(404, "template not found");
        return respPolicy.build(200, "", conf.query);
    }

    @POST
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "add new template")
    public Response post(CrawlTemplate conf) {
        conf.createdTime = conf.updatedTime = new Date().getTime();
        repo.persist(conf);
        return resp.build(200, "", conf);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "update a template")
    public Response update(
        @PathParam("id") Long id,
        @RequestBody CrawlTemplate entity) {
        CrawlTemplate conf = repo.findById(id);
        if (conf == null)
            return resp.build(404, "template not found");

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
    @Operation(summary = "delete a template")
    public Response delete (@PathParam("id") Long id) {
        CrawlTemplate conf = repo.findById(id);
        if (conf == null)
            return resp.build(404, "Template not found");
        if (repo.deleteById(id))
            return resp.build(200, "", conf);
        else
            return resp.build(500, "Fail to delete template", conf);
    }
}
