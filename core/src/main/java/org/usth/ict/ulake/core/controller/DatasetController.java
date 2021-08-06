package org.usth.ict.ulake.core.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.core.model.LakeDataset;
import org.usth.ict.ulake.core.persistence.DatasetRepository;
import org.usth.ict.ulake.core.persistence.GenericDAO;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/dataset")
@Produces(MediaType.APPLICATION_JSON)
public class DatasetController {
    private static final Logger log = LoggerFactory.getLogger(DatasetController.class);

    @Inject
    DatasetRepository datasetRepo;

    @GET
    public List<LakeDataset> all() {
        return datasetRepo.listAll();
    }
}
