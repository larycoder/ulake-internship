package org.usth.ict.ulake.core.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.core.backend.impl.OpenIO;
import org.usth.ict.ulake.core.model.LakeGroup;
import org.usth.ict.ulake.core.model.LakeHttpResponse;
import org.usth.ict.ulake.core.model.LakeObject;
import org.usth.ict.ulake.core.model.LakeObjectMetadata;
import org.usth.ict.ulake.core.persistence.GroupRepository;
import org.usth.ict.ulake.core.persistence.ObjectRepository;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Path("/object")
public class ObjectResource {
    private static final Logger log = LoggerFactory.getLogger(ObjectResource.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    OpenIO fs;

    @Inject
    ObjectRepository objectRepo;

    @Inject
    GroupRepository groupRepo;

    @Inject
    LakeHttpResponse lakeResponse;

    @GET
    public List<LakeObject> all() {
        return objectRepo.listAll();
    }

    @GET
    @Path("/{cid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response one(@PathParam("cid") String cid) {
        LakeObject object = objectRepo.find("cid", cid).firstResult();
        if (object == null) {
            return lakeResponse.build(404);
        }
        return lakeResponse.build(200, "", object);
    }

    @GET
    @Path("/{cid}/data")
    //@Produces(MediaType.APPLICATION_OCTET_STREAM_TYPE)
    public Response data(@Context HttpHeaders headers,
                         @PathParam("cid") String cid) {
        LakeObject object = objectRepo.find("cid", cid).firstResult();
        if (object == null) {
            return lakeResponse.build(404);
        }
        InputStream is = fs.get(cid);
        if (is == null) {
            return lakeResponse.build(403);
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
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(MultipartFormDataInput input) throws IOException {
        // manual workaround to void RESTEASY007545 bug
        LakeObjectMetadata meta = null;
        InputStream is = null;

        // iterate through form data to extract metadata and file
        Map<String, List<InputPart>> formDataMap = input.getFormDataMap();
        for (var formData: formDataMap.entrySet()) {
            // log.info("POST: {} {}", formData.getKey(), formData.getValue().get(0).getBodyAsString());
            if (formData.getKey().equals("metadata")) {
               try {
                   String metaJson = formData.getValue().get(0).getBodyAsString();
                   meta = mapper.readValue(metaJson, LakeObjectMetadata.class);
                } catch (JsonProcessingException e) {
                   log.info("error parsing metadata json {}", e.getMessage());
                }
            }
            else if (formData.getKey().equals("file")) {
                is = formData.getValue().get(0).getBody(InputStream.class, null);
            }
        }
        if (meta == null || is == null) {
            return lakeResponse.build(403);
        }

        // make a new object, if any
        LakeGroup group = null;
        if (meta.getGroupId() != null) {
            group = groupRepo.find("gid", meta.getGroupId()).firstResult();
        }

        // save to backend
        String cid = fs.create(meta.getName(), meta.getLength(), is);
        log.info("POST: object storage returned cid={}", cid);

        // save a new object to metadata RDBMS
        LakeObject object = new LakeObject();
        object.setCid(cid);
        Long now = new Date().getTime();
        object.setCreateTime(now);
        object.setAccessTime(now);
        object.setParentId(0L);
        object.setGroup(group);
        objectRepo.persist(object);

        JsonNode node = mapper.valueToTree(object);
        return lakeResponse.build(200, null, node);
    }
}
