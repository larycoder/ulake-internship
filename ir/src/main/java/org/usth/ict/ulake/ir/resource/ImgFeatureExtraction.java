package org.usth.ict.ulake.ir.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.security.RolesAllowed;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

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

    InputStream inputFile;
    // only file id
    try {
      OutputStream outFile = Files.newOutputStream(Paths.get("output.jpeg"));
      inputFile = coreService.objectDataByFileId(fileId, bearer);
      long length = inputFile.transferTo(outFile);

    } catch(Exception e) {
      return response.build(500, e.getMessage());
    }
    // read img data from stream
    // TODO: add extract algorithm 
    // var stream = new StreamingOutput() {
    //   @Override
    //   public void write(OutputStream os) throws IOException {
    //     inputFile.transferTo(os);
    //   }
    // };
    return response.build(200, "");
  }
}