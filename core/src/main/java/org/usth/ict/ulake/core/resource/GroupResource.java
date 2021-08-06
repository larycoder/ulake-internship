package org.usth.ict.ulake.core.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.core.backend.impl.OpenIO;
import org.usth.ict.ulake.core.model.LakeGroup;
import org.usth.ict.ulake.core.model.LakeHttpResponse;
import org.usth.ict.ulake.core.persistence.GroupRepository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
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
    LakeHttpResponse lakeResponse;

    @GET
    public List<LakeGroup> all() {
        return groupRepo.listAll();
    }

    @GET
    @Path("/{id}")
    public LakeGroup one(@PathParam("id") Long id) {
        return groupRepo.findById(id);
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(LakeGroup group) {
        group.gid = UUID.randomUUID().toString();
        groupRepo.persist(group);
        return lakeResponse.build(200, "", group);
    }

    @GET
    @Path("/list/{path}")
    public LakeGroup listByPath(@PathParam("path") String path) {
        log.info("{}: {}", path, fs.ls(path));
        return null;
    }
}
