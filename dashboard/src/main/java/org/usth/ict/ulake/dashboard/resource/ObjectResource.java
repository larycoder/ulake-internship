package org.usth.ict.ulake.dashboard.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.core.ObjectModel;
import org.usth.ict.ulake.common.service.CoreService;
import org.usth.ict.ulake.dashboard.filter.FilterModel;
import org.usth.ict.ulake.dashboard.filter.QueryException;
import org.usth.ict.ulake.dashboard.filter.impl.FilterServiceImpl;
import org.usth.ict.ulake.dashboard.model.BearerData;

@Path("/object")
@Tag(name = "Object")
public class ObjectResource {
    private static final Logger log = LoggerFactory.getLogger(ObjectResource.class);

    @Inject
    JsonWebToken jwt;

    @Inject
    JWTParser parser;

    @Inject
    ObjectMapper mapper;

    @Inject
    @RestClient
    CoreService coreSvc;

    @Inject
    LakeHttpResponse resp;

    @GET
    @RolesAllowed({"Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "list objects of user")
    public Response object(
        @QueryParam("filter") List<String> filterStr) {
        String bearer = "bearer " + jwt.getRawToken();

        // collect filters
        var filters = new ArrayList<FilterModel>();
        for (String f : filterStr) {
            filters.add(new FilterModel(f));
        }

        // apply filters
        var filterSvc = new FilterServiceImpl<ObjectModel>();
        var objResp =  coreSvc.objectList(bearer);
        var type = new TypeReference<List<ObjectModel>>() {};
        var objects = mapper.convertValue(objResp.getResp(), type);

        try {
            for (FilterModel filter : filters) {
                objects = filterSvc.filter(objects, filter);
            }
        } catch (QueryException e) {
            return resp.build(400, e.toString());
        }

        return resp.build(200, null, objects);
    }

    @GET
    @Path("/{cid}/data")
    @RolesAllowed({"Admin"})
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Operation(summary = "get object data")
    public Response objectData(@PathParam("cid") String cid) {
        String bearer = "Bearer " + jwt.getRawToken();
        InputStream is = coreSvc.objectData(cid, bearer);
        var stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException {
                is.transferTo(os);
            }
        };
        return Response.ok(stream).build();
    }

    @GET
    @Path("/{fileId}/fileData")
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "get object data by file id")
    public Response objectDataByFileId(@PathParam("fileId") Long fileId) {
        String bearer = "Bearer " + jwt.getRawToken();
        InputStream is;
        try {
            is = coreSvc.objectDataByFileId(fileId, bearer);
        } catch(Exception e) {
            return resp.build(500, "Internal error");
        }
        var stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException {
                is.transferTo(os);
            }
        };
        return Response.ok(stream).build();
    }

    @POST
    @Path("/{fileId}/fileData")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Operation(summary = "get object data by file id. POST body is in form 'bearer=token' This is used for downloading binary files without bearer in the header.")
    public Response objectDataByFileIdPOST(@PathParam("fileId") Long fileId,
        @FormParam("bearer") String bearer) {
        // verify the passed bearer jwt
        JsonWebToken localJwt;
        try {
            localJwt = parser.parse(bearer);
        } catch (ParseException e1) {
            e1.printStackTrace();
            return resp.build(401);
        }
        bearer = "Bearer " + localJwt.getRawToken();

        // get binary data from core
        InputStream is;
        try {
            is = coreSvc.objectDataByFileId(fileId, bearer);
        } catch(Exception e) {
            return resp.build(500, "Internal error");
        }
        var stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException {
                is.transferTo(os);
            }
        };
        return Response.ok(stream).build();
    }
}
