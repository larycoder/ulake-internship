package org.usth.ict.ulake.ir.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
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
import org.usth.ict.ulake.common.model.ir.DistanceRes;
import org.usth.ict.ulake.common.service.CoreService;
import org.usth.ict.ulake.common.service.FileService;
import org.usth.ict.ulake.ir.model.ImgFeature;
import org.usth.ict.ulake.ir.persistence.ImgFeatureRepo;
import org.usth.ict.ulake.ir.service.HistogramCal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    return response.build(200, "", newImg.id);
  }



  @GET
  @Path("/search/{id}")
  @Operation(summary = "Search image")
  @RolesAllowed({ "User", "Admin" })
  @Transactional
  public Response searchImage(@HeaderParam("Authorization") String bearer, @PathParam("id") Long fileId) {
    List<Integer> iFeatureVal = new ArrayList<>();
    List<DistanceRes> resultList = new ArrayList<>();

    var mapper = new ObjectMapper();
    var typeRef = new TypeReference<List<Integer>>() {
    };
    try {
      ImgFeature input = repo.find("fid", fileId).firstResult();

      if (input != null)
        iFeatureVal = mapper.readValue(input.featureValue, typeRef);
      else
        return response.build(404);

    } catch (IOException e) {
      e.printStackTrace();
      return response.build(500, "file id not exist");
    }

    try {
      for (ImgFeature i : repo.listAll()) {
        var newRes = new DistanceRes();
        double result = calculateDistance(mapper.readValue(i.featureValue, typeRef), iFeatureVal);

        newRes.fid = i.fid;
        newRes.distance = result;
        resultList.add(newRes);
      }
      resultList.sort((o1, o2) -> Double.compare(o1.distance, o2.distance));
      resultList = resultList.subList(0, resultList.size() > 10 ? 10 : resultList.size());

    } catch (IOException e) {
      e.printStackTrace();
      return response.build(500, "Cannot get result list");
    }

    return response.build(200, "", resultList);
  }

  private double calculateDistance(List<Integer> array1, List<Integer> array2) {
    double Sum = 0.0;
    for (int i = 0; i < array1.size(); i++) {
      Sum = Sum + Math.pow((array1.get(i) - array2.get(i)), 2.0);
    }
    return Math.sqrt(Sum);
  }
}
