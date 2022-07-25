package org.usth.ict.ulake.common.service;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.acl.Acl;

@Path("/api/acl")
@RegisterRestClient(configKey = "acl-api")
@RegisterProvider(value = LakeServiceExceptionMapper.class)
@Produces(MediaType.APPLICATION_JSON)
public interface AclService {
    @POST
    @Path("/validation/file")
    @Schema(description = "validate file")
    public LakeHttpResponse<Boolean> validateFile(
        @HeaderParam("Authorization") String bearer, @RequestBody Acl file);

    @POST
    @Path("/validation/folder")
    @Schema(description = "validate folder")
    public LakeHttpResponse<Boolean> validateFolder(
        @HeaderParam("Authorization") String bearer, @RequestBody Acl folder);
}
