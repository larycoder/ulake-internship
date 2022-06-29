package org.usth.ict.ulake.common.service;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.log.LogModel;

@Path("/api")
@RegisterRestClient(configKey = "log-api")
@RegisterProvider(value = LakeServiceExceptionMapper.class)
@Produces(MediaType.APPLICATION_JSON)
public interface LogService {
    @GET
    @Path("/log")
    public LakeHttpResponse all(
        @HeaderParam("Authorization") String bearer);

    @GET
    @Path("/log/{id}")
    public LakeHttpResponse one(
        @HeaderParam("Authorization") String bearer,
        @PathParam("id") @Parameter(description = "Logid to search") Long id);

    @GET
    @Path("/log/user/{id}")
    public LakeHttpResponse byUser(
        @HeaderParam("Authorization") String bearer,
        @PathParam("uid") @Parameter(description = "User id search") Long uid);

    @GET
    @Path("/log/user")
    public LakeHttpResponse byCurrentUser(
        @HeaderParam("Authorization") String bearer);

    @GET
    @Path("/log/from/{ts1}/to/{ts2}")
    public LakeHttpResponse byTime(
        @HeaderParam("Authorization") String bearer,
        @PathParam("ts1") @Parameter(description = "From timestamp") Long ts1,
        @PathParam("ts2") @Parameter(description = "To timestamp") Long ts2);

    @GET
    @Path("/log/tag")
    public LakeHttpResponse byTag(
        @HeaderParam("Authorization") String bearer,
        @QueryParam("q") @Parameter(description = "Tag to search") String tag);


    @GET
    @Path("/log/service")
    public LakeHttpResponse byService(
        @HeaderParam("Authorization") String bearer,
        @QueryParam("q") @Parameter(description = "Service to search") String service);

    @POST
    @Path("/log")
    public LakeHttpResponse post(
        @HeaderParam("Authorization") String bearer,
        LogModel log);
}
