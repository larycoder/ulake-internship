package org.usth.ict.ulake.dashboard.resource.grpcResouce;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import io.quarkus.example.*;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Multi;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.LakeHttpResponse;

import org.usth.ict.ulake.common.model.core.ObjectFormModel;
import org.usth.ict.ulake.common.model.core.ObjectModel;
import org.usth.ict.ulake.common.model.folder.FileModel;
import org.usth.ict.ulake.common.model.dashboard.FileFormModel;
import org.usth.ict.ulake.dashboard.model.ObjectMeta;
import org.usth.ict.ulake.dashboard.service.IrFeatureExtractJob;
import org.usth.ict.ulake.dashboard.service.IrFeatureExtractTask;

import java.io.IOException;
import java.util.Date;


@Path("/object")
@Tag(name = "Object Storage")
public class GrpcFileClientResource {
    private static final Logger log = LoggerFactory.getLogger(GrpcFileClientResource.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    IrFeatureExtractTask irTask;

    @GrpcClient
    CoreGrpcService coreGrpcClient;

    @GrpcClient
    CoreGrpcService coreChunkGrpcClient;

    @GrpcClient
    FileGrpcServiceGrpc.FileGrpcServiceBlockingStub fileGrpcClient;

    @Inject
    LakeHttpResponse resp;

    @Inject
    JsonWebToken jwt;

    @POST
    @Path("/test-grpc-file")
    public Response test() {
        UploadFileRequest fileRequest = UploadFileRequest.newBuilder()
                .setBearer("Bearer " + jwt.getRawToken()).build();
        int code = fileGrpcClient.test(fileRequest).getCode();
        String message = fileGrpcClient.test(fileRequest).getMessage();
        String token = fileGrpcClient.test(fileRequest).getToken();
        return resp.build(code, message, token);
    }

    @POST
    @Path("/test-grpc-core")
    public Response testCore() {
        int code = coreGrpcClient.testCoreConnect(UploadCoreRequest.newBuilder().setBearer("Bearer " + jwt.getRawToken()).build()).onItem().transform(UploadCoreResponse::getCode).await().indefinitely();
        String message = coreGrpcClient.testCoreConnect(UploadCoreRequest.newBuilder().setBearer("Bearer " + jwt.getRawToken()).build()).onItem().transform(UploadCoreResponse::getMessage).await().indefinitely();
        String token = coreGrpcClient.testCoreConnect(UploadCoreRequest.newBuilder().setBearer("Bearer " + jwt.getRawToken()).build()).onItem().transform(UploadCoreResponse::getToken).await().indefinitely();
        return resp.build(code, message, token);
    }

    @POST
    @Path("/grpc-file")
    @RolesAllowed({"User", "Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(summary = "upload new file")
    public Response post(@RequestBody(description = "file form model")
                         @MultipartForm FileFormModel input) {
        String bearer = "Bearer " + jwt.getRawToken();

        FileModel fileInfo = input.fileInfo;
        fileInfo.ownerId = Long.parseLong(jwt.getClaim(Claims.sub));
        var output = new ObjectFormModel();
        output.is = input.is;

        try {
            var objMeta = new ObjectMeta();
            objMeta.name = fileInfo.name;
            objMeta.length = fileInfo.size;
            output.metadata = mapper.writeValueAsString(objMeta);
        } catch (JsonProcessingException e) {
            output.metadata = null;
            log.info("Could not parse metadata to string ", e);
        }

        if (output.is == null || output.metadata == null || fileInfo == null) {
            return resp.build(403, "Invalid arguments");
        }

//        ObjectModel obj;
//        try {
//            var objJson = coreSvc.newObject(bearer, output).getResp();
//            obj = mapper.convertValue(objJson, ObjectModel.class);
//        } catch (Exception e) {
//            log.error("Fail to create new object", e);
//            return resp.build(500, "Fail to create new object", e.getMessage());
//        }

        ObjectModel obj = new ObjectModel();
//        var objJson = "";
        obj.cid = "null";
        try {
//            byte[] bytes = new byte[4*1024*1024];
//            int len;
//            while ((len = input.is.read(bytes)) != -1) {
//                ByteString byteString = ByteString.copyFrom(bytes, 0, len);
//                objJson = coreChunkGrpcClient.uploadCoreChunk(Multi.createFrom().item(UploadCoreChunkRequest.newBuilder()
//                        .setBearer("Bearer " + jwt.getRawToken())
//                        .setOutput(io.quarkus.example.ObjectFormModelChunk.newBuilder()
//                                .setMetadata(output.metadata)
//                                .setIs(byteString)
//                                .setFileID(obj.cid)
//                                .build())
//                        .build())).onItem().transform(UploadCoreResponse::getToken).await().indefinitely();
//                obj.cid = objJson;
//            }
            var objJson = coreGrpcClient.uploadCore(Multi.createFrom().item(UploadCoreRequest.newBuilder()
                    .setBearer("Bearer " + jwt.getRawToken())
                    .setOutput(io.quarkus.example.ObjectFormModel.newBuilder()
                            .setMetadata(output.metadata)
                            .setIs(ByteString.copyFrom(IOUtils.toByteArray(input.is)))
                            .build())
                    .build())).onItem().transform(UploadCoreResponse::getToken).await().indefinitely();
            obj.cid = objJson;

            FileModel fileModel = new FileModel();
            String fileResp;
            try {
                fileInfo.cid = obj.cid;
                fileInfo.creationTime = new Date().getTime();
                fileResp = fileGrpcClient.uploadFile(UploadFileRequest.newBuilder()
                        .setBearer(bearer)
                        .setOutput(io.quarkus.example.FileModel.newBuilder()
                                .setName(fileInfo.name)
                                .setOwnerId(fileInfo.ownerId)
                                .setSize(fileInfo.size)
                                .setMime(fileInfo.mime)
                                .setCid(fileInfo.cid)
                                .setCreationTime(fileInfo.creationTime)
                                .build())
                        .build()).getToken();

            } catch (Exception e) {
                log.error("Fail to create new file from object", e);
                return resp.build(500, "Fail to create new file from object");
            }

            // check mime and perform feature extraction
            if (fileModel.mime != null && fileModel.mime.startsWith("image/")) {
                try {
                    irTask.start(bearer, fileModel.id, IrFeatureExtractJob.class);
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
            }

            var jsonResp = fileResp.split(" ");
            var fileRespJson = new FileModel();
            fileRespJson.cid = jsonResp[0];
            fileRespJson.id = Long.parseLong(jsonResp[1]);
            fileRespJson.mime = jsonResp[2];
            fileRespJson.name = jsonResp[3];
            fileRespJson.ownerId = Long.parseLong(jsonResp[4]);
            fileRespJson.size = Long.parseLong(jsonResp[5]);
            fileRespJson.creationTime = Long.parseLong(jsonResp[6]);
            fileRespJson.parent = null;

            return resp.build(200, null, fileRespJson);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
