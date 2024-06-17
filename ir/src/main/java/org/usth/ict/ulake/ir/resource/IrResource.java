package org.usth.ict.ulake.ir.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.ir.DistanceRes;
import org.usth.ict.ulake.common.service.CoreService;
import org.usth.ict.ulake.common.service.FileService;
import org.usth.ict.ulake.ir.model.ImgFeature;
import org.usth.ict.ulake.ir.persistence.IrRepo;
import org.usth.ict.ulake.ir.service.GLCM;
import org.usth.ict.ulake.ir.service.Histogram;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/ir")
@Produces(MediaType.APPLICATION_JSON)
public class IrResource {
    private static final Logger log = LoggerFactory.getLogger(IrResource.class);

    @Inject
    LakeHttpResponse<Object> response;

    @Inject
    @RestClient
    FileService fileService;

    @Inject
    @RestClient
    CoreService coreService;

    @Inject
    IrRepo repo;

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/extract/{id}")
    @Operation(summary = "Perform feature extraction on an uploaded image file")
    @RolesAllowed({ "User", "Admin" })
    @Transactional
    public Response extract(@HeaderParam("Authorization") String bearer, @PathParam("id") Long fileId) {

        Long jwtUserId = Long.parseLong(jwt.getClaim(Claims.sub));
        var isFileExist = repo.find("fid = ?1 and uid = ?2", fileId, jwtUserId).list().size() > 0;

        if (isFileExist)
            return response.build(409, "File Existed");

        InputStream inputFile;

        try {
            OutputStream outFile = Files.newOutputStream(Paths.get("/tmp/output.jpeg"));
            inputFile = coreService.objectDataByFileId(fileId, bearer);
            inputFile.transferTo(outFile);

        } catch (IOException e) {
            return response.build(500, "Cannot get File");
        }

        Histogram histo = new Histogram();
        String imgHistValue = histo.calc("/tmp/output.jpeg").toString();

        GLCM glcm = new GLCM();
        String imgGlcmValue = glcm.extract("/tmp/output.jpeg").toString();

        var newImg = new ImgFeature();
        newImg.fid = fileId;
        newImg.featureValueHist = imgHistValue;
        newImg.featureValueGLCM = imgGlcmValue;
        newImg.uid = jwtUserId;
        repo.persist(newImg);
        return response.build(200, "", newImg.id);
    }

    @GET
    @Path("/search/{id}")
    @Operation(summary = "Search for images similar to a given one using hist")
    @RolesAllowed({ "User", "Admin" })
    @Transactional
    public Response searchHist(@HeaderParam("Authorization") String bearer, @PathParam("id") Long fileId) {
        List<Double> iFeatureVal = new ArrayList<>();
        List<DistanceRes> resultList = new ArrayList<>();

        Long jwtUserId = Long.parseLong(jwt.getClaim(Claims.sub));

        var mapper = new ObjectMapper();
        var typeRef = new TypeReference<List<Double>>() {
        };

        try {
            ImgFeature input = repo.find("fid", fileId).firstResult();

            if (input != null) {
                if (!input.uid.equals(jwtUserId))
                    return response.build(304);

                iFeatureVal = mapper.readValue(input.featureValueHist, typeRef);
            } else
                return response.build(404);
        } catch (IOException e) {
            e.printStackTrace();
            return response.build(500, "file id not exist");
        }

        try {
            for (ImgFeature i : repo.listAll()) {
                var newRes = new DistanceRes();
                String feature = i.featureValueHist;
                if (feature == null) feature = i.featureValue;
                if (feature == null) continue;
                double result = calculateDistance(mapper.readValue(feature, typeRef), iFeatureVal);
                if (!i.fid.equals(fileId) && jwtUserId.equals(i.uid)) {
                    newRes.fid = i.fid;
                    newRes.distance = result;
                    resultList.add(newRes);
                }
            }

            resultList.sort((o1, o2) -> Double.compare(o1.distance, o2.distance));
            resultList = resultList.subList(0, resultList.size() > 10 ? 10 : resultList.size());

        } catch (IOException e) {
            e.printStackTrace();
            return response.build(500, "Cannot get result list");
        }

        return response.build(200, "", resultList);
    }


    @GET
    @Path("/search/{id}/glcm")
    @Operation(summary = "Search for images similar to a given one using glcm")
    @RolesAllowed({ "User", "Admin" })
    @Transactional
    public Response searchGlcm(@HeaderParam("Authorization") String bearer, @PathParam("id") Long fileId) {
        List<Double> iFeatureVal = new ArrayList<>();
        List<DistanceRes> resultList = new ArrayList<>();

        Long jwtUserId = Long.parseLong(jwt.getClaim(Claims.sub));

        var mapper = new ObjectMapper();
        var typeRef = new TypeReference<List<Double>>() {
        };

        try {
            ImgFeature input = repo.find("fid", fileId).firstResult();

            if (input != null) {
                if (!input.uid.equals(jwtUserId))
                    return response.build(304);
                iFeatureVal = mapper.readValue(input.featureValueGLCM, typeRef);
            } else
                return response.build(404);
        } catch (IOException e) {
            e.printStackTrace();
            return response.build(500, "file id not exist");
        }

        try {
            for (ImgFeature i : repo.listAll()) {
                var newRes = new DistanceRes();
                double result = calculateDistance(mapper.readValue(i.featureValueGLCM, typeRef), iFeatureVal);
                if (!i.fid.equals(fileId) && jwtUserId.equals(i.uid)) {
                    newRes.fid = i.fid;
                    newRes.distance = result;
                    resultList.add(newRes);
                }
            }

            resultList.sort((o1, o2) -> Double.compare(o1.distance, o2.distance));
            resultList = resultList.subList(0, resultList.size() > 10 ? 10 : resultList.size());

        } catch (IOException e) {
            e.printStackTrace();
            return response.build(500, "Cannot get result list");
        }

        return response.build(200, "", resultList);
    }

    private double calculateDistance(List<Double> array1, List<Double> array2) {
        double Sum = 0.0;
        for (int i = 0; i < array1.size(); i++) {
            Sum = Sum + Math.pow((array1.get(i) - array2.get(i)), 2.0);
        }
        return Math.sqrt(Sum);
    }
}
