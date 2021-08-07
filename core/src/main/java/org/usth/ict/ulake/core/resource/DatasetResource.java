package org.usth.ict.ulake.core.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.core.model.LakeDataset;
import org.usth.ict.ulake.core.model.LakeHttpResponse;
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

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(LakeDataset dataset) {
        repo.persist(dataset);
        return response.build(200, "", dataset);
    }
}
