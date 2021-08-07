package org.usth.ict.ulake.core.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.core.backend.impl.OpenIO;
import org.usth.ict.ulake.core.model.LakeGroup;
import org.usth.ict.ulake.core.model.LakeHttpResponse;
import org.usth.ict.ulake.core.persistence.GroupRepository;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/group")
@Produces(MediaType.APPLICATION_JSON)
public class GroupResource {
    private static final Logger log = LoggerFactory.getLogger(GroupResource.class);

    @Inject
    OpenIO fs;

    @Inject
    GroupRepository groupRepo;

    @Inject
    LakeHttpResponse response;

    @GET
    public Response all() {
        return response.build(200, null, groupRepo.listAll());
    }

    @GET
    @Path("/{id}")
    public Response one(@PathParam("id") Long id) {
        return response.build(200, null, groupRepo.findById(id));
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(LakeGroup group) {
        group.gid = UUID.randomUUID().toString();
        groupRepo.persist(group);
        return response.build(200, "", group);
    }

    @GET
    @Path("/list/{path}")
    public Response listByPath(@PathParam("path") String path) {
        log.info("{}: {}", path, fs.ls(path));
        return response.build(200, null, fs.ls(path));
    }
}
