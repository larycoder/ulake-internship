package org.usth.ict.ulake.ir.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.security.RolesAllowed;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.transaction.Transactional;
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
import org.usth.ict.ulake.common.service.CoreService;
import org.usth.ict.ulake.common.service.FileService;
import org.usth.ict.ulake.common.service.exception.LakeServiceException;
import org.usth.ict.ulake.common.service.exception.LakeServiceForbiddenException;
import org.usth.ict.ulake.common.service.exception.LakeServiceInternalException;
import org.usth.ict.ulake.ir.model.ImgFeature;
import org.usth.ict.ulake.ir.persistence.ImgFeatureRepo;
import org.usth.ict.ulake.ir.service.HistogramCal;

@Path("/ir")
@Produces(MediaType.APPLICATION_JSON)
public class ImgFeatureExtraction {

  @Inject
  LakeHttpResponse<Object> response;

  @Inject
  @RestClient
  FileService fileService;

  @Inject
  @RestClient
  CoreService coreService;

  @Inject
  ImgFeatureRepo repo;

  @GET
  @Path("/extract/{id}")
  @Operation(summary = "Extract uploaded img")
  @RolesAllowed({ "User", "Admin" })
  @Transactional
  public Response extractFeature(@HeaderParam("Authorization") String bearer, @PathParam("id") Long fileId) {

    InputStream inputFile;
    try {
      OutputStream outFile = Files.newOutputStream(Paths.get("/tmp/output.jpeg"));
      inputFile = coreService.objectDataByFileId(fileId, bearer);
      inputFile.transferTo(outFile);

    } catch (IOException e) {
      return response.build(500, "Cannot get File", e.getMessage());
    }

    HistogramCal histogramCal = new HistogramCal();
    String imgValue = histogramCal.run("/tmp/output.jpeg").toString();

    var newImg = new ImgFeature();
    newImg.fid = fileId;
    newImg.featureValue = imgValue;

    repo.persist(newImg);

    return response.build(200, "Image Feature extract  succesfull", newImg.id);
  }
}