package org.usth.ict.ulake.common.service;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.acl.Acl;

@Path("/api/acl")
@RegisterRestClient(configKey = "acl-api")
@Produces(MediaType.APPLICATION_JSON)
public interface AclService {
    @GET
    @Path("/validation/file")
    @Schema(description = "validate file")
    public LakeHttpResponse validateFile(
        @HeaderParam("Authorization") String bearer, Acl file);

    @GET
    @Path("/validation/folder")
    @Schema(description = "validate folder")
    public LakeHttpResponse validateFolder(
        @HeaderParam("Authorization") String bearer, Acl folder);
}
