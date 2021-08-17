package org.usth.ict.ulake.core.resource;

import antlr.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.misc.Utils;
import org.usth.ict.ulake.core.model.LakeDataset;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.core.persistence.DatasetRepository;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/dataset")
@Produces(MediaType.APPLICATION_JSON)
public class DatasetResource {
    private static final Logger log = LoggerFactory.getLogger(DatasetResource.class);

    @Inject
    DatasetRepository repo;

    @Inject
    LakeHttpResponse response;

    @GET
    public Response all() {
        return response.build(200, "", repo.listAll());
    }

    @GET
    @Path("/{id}")
    public Response one(@PathParam("id") Long id) {
        return response.build(200, null, repo.findById(id));
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(LakeDataset dataset) {
        repo.persist(dataset);
        return response.build(200, "", dataset);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") Long id, LakeDataset newData) {
        LakeDataset entity = repo.findById(id);
        if (entity == null) {
            return response.build(404);
        }
        if (!Utils.isEmpty(newData.description)) entity.description = newData.description;
        if (!Utils.isEmpty(newData.name)) entity.name = newData.name;
        if (!Utils.isEmpty(newData.source)) entity.source = newData.source;
        if (!Utils.isEmpty(newData.licence)) entity.licence = newData.licence;
        if (!Utils.isEmpty(newData.tags)) entity.tags = newData.tags;
        repo.persist(entity);
        return response.build(200);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") Long id) {
        LakeDataset entity = repo.findById(id);
        if (entity == null) {
            return response.build(404);
        }
        repo.delete(entity);
        return response.build(200);
    }
}
