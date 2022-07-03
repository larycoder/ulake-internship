package org.usth.ict.ulake.compress.resource;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.folder.FileModel;
import org.usth.ict.ulake.common.service.CoreService;
import org.usth.ict.ulake.common.service.FileService;

@Path("/ir") 
@Produces(MediaType.APPLICATION_JSON)

public class ImgFeatureExtraction {

  @Inject
  LakeHttpResponse response;

  @Inject
  @RestClient
  FileService fileService;
  
  @Inject
  @RestClient
  CoreService coreService;

  @GET
  @Path("/extract/{id}")
  @Operation(summary = "Extract uploaded img")
  @RolesAllowed({"User", "Admin"})
  public Response extractFeature (@HeaderParam("Authorization") String bearer, @PathParam("id") Long fileId) {

    // only file id
    var inputFile = coreService.objectDataByFileId(fileId, bearer);
    // read img data from stream
    // TODO: add extract algorithm 
    var feature = "";
    return response.build(200, "", inputFile);
  }
}