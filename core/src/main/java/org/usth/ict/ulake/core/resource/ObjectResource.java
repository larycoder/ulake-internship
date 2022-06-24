package org.usth.ict.ulake.core.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.misc.AclUtil;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.PermissionModel;
import org.usth.ict.ulake.common.model.folder.FileModel;
import org.usth.ict.ulake.common.service.AclService;
import org.usth.ict.ulake.common.service.FileService;
import org.usth.ict.ulake.common.service.exception.LakeServiceForbiddenException;
import org.usth.ict.ulake.core.backend.impl.Hdfs;
import org.usth.ict.ulake.core.model.LakeGroup;
import org.usth.ict.ulake.core.model.LakeObject;
import org.usth.ict.ulake.core.model.LakeObjectMetadata;
import org.usth.ict.ulake.core.model.LakeObjectSearchQuery;
import org.usth.ict.ulake.core.persistence.GroupRepository;
import org.usth.ict.ulake.core.persistence.ObjectRepository;

@Path("/object")
@Tag(name = "Object Storage")
public class ObjectResource {
    private static final Logger log = LoggerFactory.getLogger(ObjectResource.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    Hdfs fs;

    @Inject
    ObjectRepository repo;

    @Inject
    GroupRepository groupRepo;

    @Inject
    LakeHttpResponse response;

    @Inject
    JsonWebToken jwt;

    @Inject
    @RestClient
    FileService fileSvc;

    @Inject
    @RestClient
    AclService aclSvc;

    @GET
    @RolesAllowed({ "Admin" })
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "List all objects")
    public Response all() {
        return response.build(200, null, repo.listAll());
    }

    @GET
    @Path("/{cid}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "Admin" })
    @Operation(summary = "Get one object info")
    public Response one(@PathParam("cid") @Parameter(description = "Content id to lookup") String cid) {
        LakeObject object = repo.find("cid", cid).firstResult();
        if (object == null) {
            return response.build(404);
        }
        return response.build(200, "", object);
    }

    @GET
    @Path("/{cid}/data")
    @RolesAllowed({ "Admin" })
    @Operation(summary = "Get object binary data")
    public Response data(
        @PathParam("cid")
        @Parameter(description = "Content id to extract")
        String cid) {
        return streamOutData(cid);
    }

    @GET
    @Path("/{fileId}/fileData")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Get object binary data")
    public Response dataByFileId(
        @PathParam("fileId")
        @Parameter(description = "File id to extract")
        Long fileId) {
        var filePermit = PermissionModel.READ; // <-- permit
        String cid;

        /* Validate and retrieve cid from file */
        try {
            var fileResp = fileSvc.fileInfo(
                               fileId, "bearer " + jwt.getRawToken());
            if (fileResp == null || fileResp.getResp() == null)
                return response.build(404, "File not found");

            var file = mapper.convertValue(
                           fileResp.getResp(), FileModel.class);
            if (!AclUtil.verifyFileAcl(
                        aclSvc, jwt, file.id, file.ownerId, filePermit))
                return response.build(403);

            cid = file.cid;
        } catch (LakeServiceForbiddenException e) {
            return response.build(403, "File forbidden");
        } catch (Exception e) {
            log.error("File process error", e);
            return response.build(500, "Internal error");
        }

        return streamOutData(cid);
    }

    @POST
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Create a new binary object")
    public Response post(
        @RequestBody(description = "Multipart form data. metadata: extra json info " +
                                   "{name:'original filename', gid: 'object group id', length: 'binary length'). file: binary data to save")
        MultipartFormDataInput input) throws IOException {
        // TODO: check ACL
        // manual workaround to void RESTEASY007545 bug
        LakeObjectMetadata meta = null;
        InputStream is = null;

        // iterate through form data to extract metadata and file
        Map<String, List<InputPart>> formDataMap = input.getFormDataMap();
        for (var formData : formDataMap.entrySet()) {
            // log.info("POST: {} {}", formData.getKey(), formData.getValue().get(0).getBodyAsString());
            if (formData.getKey().equals("metadata")) {
                try {
                    String metaJson = formData.getValue().get(0).getBodyAsString();
                    meta = mapper.readValue(metaJson, LakeObjectMetadata.class);
                } catch (JsonProcessingException e) {
                    log.error("error parsing metadata json {}", e.getMessage());
                }
            } else if (formData.getKey().equals("file")) {
                is = formData.getValue().get(0).getBody(InputStream.class, null);
            }
        }

        if (meta == null || is == null) {
            return response.build(403);
        }

        // make a new object, if any
        LakeGroup group = null;
        if (meta.getGroupId() != null) {
            group = groupRepo.find("gid", meta.getGroupId()).firstResult();
        }

        // save to backend
        String cid = fs.create(meta.getName(), meta.getLength(), is);
        log.info("POST: object storage returned cid={}", cid);
        if (cid == null)
            return response.build(500, "Internal error");

        // save a new object to metadata RDBMS
        LakeObject object = new LakeObject();
        object.setCid(cid);
        Long now = new Date().getTime();
        object.setCreateTime(now);
        object.setAccessTime(now);
        object.setParentId(0L);
        object.setGroup(group);
        repo.persist(object);
        return response.build(200, null, object);
    }

    @POST
    @Path("/search")
    @RolesAllowed({ "Admin"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Search for object")
    public Response search(
        @RequestBody(description = "Query to perform search for objects")
        LakeObjectSearchQuery query) {
        var result = repo.search(query);

        if (result.isEmpty())
            return response.build(404);
        else
            return response.build(200, null, result);
    }

    @GET
    @RolesAllowed({ "Admin" })
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get statistics about objects")
    @Path("/stats")
    public Response stats() {
        var stats = fs.stats();
        return response.build(200, null, stats);
    }

    /**
     * Query and stream data as http response
     *
     * @param cid - content id of object
     * @return - response error or response stream relying on query result
     */
    private Response streamOutData(String cid) {
        LakeObject object = repo.find("cid", cid).firstResult();
        if (object == null) {
            return response.build(404);
        }
        InputStream is = fs.get(cid);
        if (is == null) {
            return response.build(403);
        }
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException {
                is.transferTo(os);
            }
        };
        return Response.ok(stream).build();
    }
}
