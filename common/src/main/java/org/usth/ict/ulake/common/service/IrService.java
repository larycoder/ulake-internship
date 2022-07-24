package org.usth.ict.ulake.common.service;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.ir.DistanceRes;

@Path("/api/ir")
@RegisterRestClient(configKey = "ir-api")
@RegisterProvider(value = LakeServiceExceptionMapper.class)
@Produces(MediaType.APPLICATION_JSON)
public interface IrService {
    @GET
    @Path("/search/{id}")
    public LakeHttpResponse<List<DistanceRes>> search(
        @HeaderParam("Authorization") String bearer,
        @PathParam("id") @Parameter(description = "File id to search") Long fileId);

    @GET
    @Path("/extract/{id}")
    public LakeHttpResponse<Long> extract(
        @HeaderParam("Authorization") String bearer,
        @PathParam("id") @Parameter(description = "File id to extract") Long fileId);
}
