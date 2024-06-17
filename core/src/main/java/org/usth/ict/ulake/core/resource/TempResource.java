package org.usth.ict.ulake.core.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.log.LogModel;
import org.usth.ict.ulake.common.service.LogService;
import org.usth.ict.ulake.core.backend.impl.Hdfs;

@Path("/temp")
@Tag(name = "Temp Storage")
public class TempResource {
    private static final Logger log = LoggerFactory.getLogger(TempResource.class);

    @ConfigProperty(name = "hdfs.core.temp")
    String tempDir;

    @Inject
    Hdfs fs;

    @Inject
    JsonWebToken jwt;

    @Inject
    LakeHttpResponse<String> response;

    @Inject
    @RestClient
    LogService logService;

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Create a new temporary object")
    public Response post(
        @HeaderParam("Authorization") String bearer,
        InputStream is) throws IOException {
        if (is == null) {
            return response.build(403);
        }

        // save to backend
        UUID uuid = UUID.randomUUID();
        String cid = fs.create(tempDir, uuid.toString(), 0, is);
        log.info("POST: temp storage returned cid={}", cid);
        if (cid == null)
            return response.build(500, "Internal error");

        logService.post(bearer, new LogModel("Add", "Added a new temp file with name " + uuid.toString()));
        return response.build(200, null, cid);
    }

    @GET
    @Path("/{cid}/data")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Get object binary data")
    public Response data(
        @HeaderParam("Authorization") String bearer,
        @PathParam("cid")
        @Parameter(description = "Content id to extract")
        String cid) {
        logService.post(bearer, new LogModel("Extract", "Get temp data for cid " + cid));
        return streamOutData(bearer, cid);
    }

    /**
     * Query and stream data as http response
     *
     * @param cid - content id of object
     * @return - response error or response stream relying on query result
     */
    private Response streamOutData(@HeaderParam("Authorization") String bearer, String cid) {
        InputStream is = fs.get(tempDir, cid);
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

    @DELETE
    @Path("/{cid}")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Delete temp binary data")
    public Response delete(
        @HeaderParam("Authorization") String bearer,
        @PathParam("cid") @Parameter(description = "Content id to delete") String cid) {
        fs.delete(tempDir, cid);
        logService.post(bearer, new LogModel("Delete", "Delete temp data for cid " + cid));
        return response.build(200, null, cid);
    }

    // TODO: cleanup temp data periodically
}
