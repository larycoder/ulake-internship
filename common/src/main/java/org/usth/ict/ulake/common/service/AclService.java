package org.usth.ict.ulake.common.service;

import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.acl.Acl;
import org.usth.ict.ulake.common.model.acl.macro.FileType;

@Path("/api/acl")
@RegisterRestClient(configKey = "acl-api")
@RegisterProvider(value = LakeServiceExceptionMapper.class)
@Produces(MediaType.APPLICATION_JSON)
public interface AclService {
    @POST
    @Path("/validation/{fileType}")
    @Schema(description = "validate file")
    public LakeHttpResponse<Boolean> validate(
        @HeaderParam("Authorization") String bearer,
        @PathParam("fileType") FileType fileType, Acl file);
}
