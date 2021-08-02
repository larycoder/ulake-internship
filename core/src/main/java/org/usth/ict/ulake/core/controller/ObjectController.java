package org.usth.ict.ulake.core.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.core.backend.FileSystem;
import org.usth.ict.ulake.core.model.*;
import org.usth.ict.ulake.core.persistence.GenericDAO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

@Path("/object")
public class ObjectController {
    private static final Logger log = LoggerFactory.getLogger(ObjectController.class);

    ObjectMapper mapper = new ObjectMapper();

    @Inject
    List<FileSystem> fs;

    @Inject
    GenericDAO<LakeObject> objectDao;

    @Inject
    GenericDAO<LakeGroup> groupDao;

    @Inject
    LakeHttpResponse lakeResponse;

    public ObjectController(List<FileSystem> fs, GenericDAO<LakeObject> objectDao, GenericDAO<LakeGroup> groupDao) {
        this.fs = fs;
        this.objectDao = objectDao;
        this.groupDao = groupDao;
        this.objectDao.setClazz(LakeObject.class);
        this.groupDao.setClazz(LakeGroup.class);
    }

    @GET
    public List<LakeObject> all() {
        return objectDao.list();
    }

    @GET
    @Path("/object/{cid}")
    @Produces(MediaType.APPLICATION_JSON)
    public LakeObject one(@PathParam("cid") String cid) {
        return objectDao.findBy("cid", cid);
    }

    @GET
    @Path("/object/data/{cid}")
    //@Produces(MediaType.APPLICATION_OCTET_STREAM_TYPE)
    public Response data(@Context HttpHeaders headers,
                         @PathParam("cid") String cid) {
        LakeObject object = objectDao.findBy("cid", cid);
        if (object == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        InputStream is = fs.get(0).get(cid);
        if (is == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException {
                is.transferTo(os);
            }
        };
        return Response.ok(stream).build();
    }

    @POST
    @Path("/object")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String post(@MultipartForm LakeObjectFormWrapper objectWrapper) {
        // extract data from POSTed multi-part form
        LakeObjectMetadata meta = objectWrapper.getMetadata();
        log.info("POST: Prepare to create object with meta {}", meta);
        LakeGroup group = null;
        if (meta.getGroupId() != 0) {
            group = groupDao.findById(meta.getGroupId());
        }

        // save to backend
        String cid = fs.get(0).create(meta.getName(), meta.getLength(), null);
        log.info("POST: object storage returned cid={}", cid);

        // save a new object to metadata RDBMS
        LakeObject object = new LakeObject();
        object.setCid(cid);
        Long now = new Date().getTime();
        object.setCreateTime(now);
        object.setAccessTime(now);
        object.setParentId(0L);
        object.setGroup(group);
        objectDao.save(object);

        JsonNode node = mapper.valueToTree(object);
        return lakeResponse.toString(200, null, node);
    }
}
