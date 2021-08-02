package org.usth.ict.ulake.core.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.core.backend.FileSystem;
import org.usth.ict.ulake.core.model.LakeGroup;
import org.usth.ict.ulake.core.model.LakeHttpResponse;
import org.usth.ict.ulake.core.persistence.GenericDAO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/group")
@Produces(MediaType.APPLICATION_JSON)
public class GroupController {
    private static final Logger log = LoggerFactory.getLogger(GroupController.class);

    @Inject
    List<FileSystem> fs;

    @Inject
    GenericDAO<LakeGroup> groupDao;

    @Inject
    LakeHttpResponse lakeResponse;

    public GroupController(List<FileSystem> fs, GenericDAO<LakeGroup> groupDao) {
        this.fs = fs;
        this.groupDao = groupDao;
        this.groupDao.setClazz(LakeGroup.class);
    }

    @GET
    public List<LakeGroup> all() {
        return groupDao.list();
    }

    @GET
    @Path("/group/{id}")
    public LakeGroup one(@PathParam("id") Long id) {
        fs.get(0).ls(String.valueOf(id));
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
        log.info("{}: {}", path, fs.get(0).ls(path));
        return null;
    }
}
