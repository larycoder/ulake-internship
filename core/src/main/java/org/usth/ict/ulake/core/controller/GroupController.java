package org.usth.ict.ulake.core.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.core.backend.impl.OpenIO;
import org.usth.ict.ulake.core.model.LakeGroup;
import org.usth.ict.ulake.core.model.LakeHttpResponse;
import org.usth.ict.ulake.core.persistence.GenericDAO;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/group")
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class GroupController {
    private static final Logger log = LoggerFactory.getLogger(GroupController.class);

    @Inject
    OpenIO fs;

    @Inject
    GenericDAO<LakeGroup> groupDao;

    //@Inject
    LakeHttpResponse lakeResponse = new LakeHttpResponse();

    public GroupController(OpenIO fs, GenericDAO<LakeGroup> groupDao) {
        this.fs = fs;
        this.groupDao = groupDao;
        this.groupDao.setClazz(LakeGroup.class);
    }

    @GET
    public List<LakeGroup> all() {
        groupDao.setClazz(LakeGroup.class);
        return groupDao.list();
    }

    @GET
    @Path("/group/{id}")
    public LakeGroup one(@PathParam("id") Long id) {
        fs.ls(String.valueOf(id));
        return groupDao.findById(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String post() {
        return lakeResponse.toString(200);
    }

    @GET
    @Path("/group/list/{path}")
    public LakeGroup listByPath(@PathParam("path") String path) {
        log.info("{}: {}", path, fs.ls(path));
        return null;
    }
}
