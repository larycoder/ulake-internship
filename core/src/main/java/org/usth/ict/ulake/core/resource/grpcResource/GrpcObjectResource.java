package org.usth.ict.ulake.core.resource.grpcResource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import io.quarkus.example.*;
import io.quarkus.grpc.GrpcService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.core.backend.impl.Localfs;
import org.usth.ict.ulake.core.model.LakeGroup;
import org.usth.ict.ulake.core.model.LakeObject;
import org.usth.ict.ulake.core.model.LakeObjectMetadata;
import org.usth.ict.ulake.core.persistence.GroupRepository;
import org.usth.ict.ulake.core.persistence.ObjectRepository;

import java.io.IOException;
import java.util.Date;

@GrpcService
public class GrpcObjectResource extends CoreGrpcServiceGrpc.CoreGrpcServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(GrpcObjectResource.class);

    @Inject
    LakeHttpResponse response;

    @ConfigProperty(name = "ulake.jwt.accesstoken.expire")
    long tokenExpire;

    @ConfigProperty(name = "ulake.jwt.refreshtoken.expire")
    long refreshTokenExpire;

    @Inject
    JsonWebToken jwt;

    @Inject
    Localfs fs;

    @Inject
    LakeHttpResponse resp;

    @Inject
    ObjectRepository repo;

    //    object mapper
    @Inject
    ObjectMapper mapper;

    @Inject
    GroupRepository groupRepo;

    @Override
    public void testCoreConnect(UploadCoreRequest request, StreamObserver<UploadCoreResponse> responseObserver) {
        responseObserver.onNext(UploadCoreResponse.newBuilder().setCode(200).setMessage("OK").setToken("true").build());
        responseObserver.onCompleted();
    }

    @POST
    @Path("/file")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
//    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Create a new binary object")
    @Override
    public StreamObserver<UploadCoreRequest> uploadCore(StreamObserver<UploadCoreResponse> responseObserver) {
        return new StreamObserver<UploadCoreRequest>() {
            @Override
            public void onNext(UploadCoreRequest request) {
                var bearer = request.getBearer();
//                var metadata = request.getOutput().getMetadata();
//                var file = request.getOutput().getIs();
                var inputs = request.getOutput();

                try {
                    var result = insertFile(bearer, inputs);
                    responseObserver.onNext(UploadCoreResponse.newBuilder().setCode(result.getCode()).setMessage(result.getMsg()).setToken(result.getResp().toString()).build());
                } catch (IOException e) {
                    log.error("error parsing multipart form data {}", e.getMessage());
                    responseObserver.onNext(UploadCoreResponse.newBuilder().setCode(500).setMessage("Internal Error").build());
                }
//                responseObserver.onNext(UploadCoreResponse.newBuilder().setCode(200).setMessage("OK").setToken("true").build());
//                responseObserver.onCompleted();
            }

            @Override
            public void onError(Throwable t) {
                log.error("error in uploadCore {}", t.getMessage());
                responseObserver.onNext(UploadCoreResponse.newBuilder().setCode(500).setMessage("Internal Error").build());
                responseObserver.onCompleted();
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    public LakeHttpResponse checkFile(String bearer, String metadata, ByteString file) throws IOException {
//        return the whole file to check
        return new LakeHttpResponse<>(200, "OK", file);
    }

    public static LakeObjectMetadata convertStringtoLakeObjectMetadata(String metadata) {
        try {
            return new ObjectMapper().readValue(metadata, LakeObjectMetadata.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public LakeHttpResponse insertFile(
            @HeaderParam("Authorization") String bearer,
            @RequestBody(description = "Multipart form data. metadata: extra json info " +
                    "{name:'original filename', gid: 'object group id', length: 'binary length'). file: binary data to save")
            ObjectFormModel input) throws IOException {
        LakeObjectMetadata meta;
        ByteString is = input.getIs();
        try {
            meta = mapper.readValue(input.getMetadata(), LakeObjectMetadata.class);
        } catch (JsonProcessingException e) {
            log.error("error parsing metadata json {}", e.getMessage());
            return new LakeHttpResponse(400, "Bad Request", null);
        }

        if (meta == null) {
            return new LakeHttpResponse(403, "Forbidden", null);
        }

        // make a new object, if any
        LakeGroup group = null;
        try{
            if (meta.getGroupId() != null) {
                group = groupRepo.find("gid", meta.getGroupId()).firstResult();
            }
        } catch (Exception e) {
            log.error("error parsing metadata json {}", e. getMessage());
            return new LakeHttpResponse(400, "Bad Request", null);
        }

        // save to backend
        String cid = fs.create(meta.getName(), meta.getLength(), is);
        log.info("POST: object storage returned cid={}", cid);
        if (cid == null) {
            return new LakeHttpResponse(500, "Internal error", null);
        }

        // save a new object to metadata RDBMS
        LakeObject object = new LakeObject();
        object.setCid(cid);
        Long now = new Date().getTime();
        object.setCreateTime(now);
        object.setAccessTime(now);
        object.setParentId(0L);
//        repo.persist(object);
        log.info("POST: object metadata saved cid={}", cid);
        return new LakeHttpResponse(200, "OK", cid);
    }

    @POST
    @Path("/file")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
//    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Create a new binary object")
    @Override
    public StreamObserver<UploadCoreChunkRequest> uploadCoreChunk(StreamObserver<UploadCoreResponse> responseObserver) {
        return new StreamObserver<UploadCoreChunkRequest>() {
            @Override
            public void onNext(UploadCoreChunkRequest request) {
                var bearer = request.getBearer();
                var inputs = request.getOutput();
                String cid = inputs.getFileID();

                try {
                    var result = insertFileChunk(bearer, inputs, cid);
                    cid = result.getResp().toString();
                    responseObserver.onNext(UploadCoreResponse.newBuilder().setCode(result.getCode()).setMessage(result.getMsg()).setToken(result.getResp().toString()).build());
                } catch (IOException e) {
                    log.error("error parsing multipart form data {}", e.getMessage());
                    responseObserver.onNext(UploadCoreResponse.newBuilder().setCode(500).setMessage("Internal Error").build());
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error("error in uploadCore {}", t.getMessage());
                responseObserver.onNext(UploadCoreResponse.newBuilder().setCode(500).setMessage("Internal Error").build());
                responseObserver.onCompleted();
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    public LakeHttpResponse insertFileChunk(
            @HeaderParam("Authorization") String bearer,
            @RequestBody(description = "Multipart form data. metadata: extra json info " +
                    "{name:'original filename', gid: 'object group id', length: 'binary length'). file: binary data to save")
            ObjectFormModelChunk input, String cid) throws IOException {
        LakeObjectMetadata meta;
        ByteString is = input.getIs();
        try {
            meta = mapper.readValue(input.getMetadata(), LakeObjectMetadata.class);
        } catch (JsonProcessingException e) {
            log.error("error parsing metadata json {}", e.getMessage());
            return new LakeHttpResponse(400, "Bad Request", null);
        }

        if (meta == null) {
            return new LakeHttpResponse(403, "Forbidden", null);
        }

        // make a new object, if any
        LakeGroup group = null;
        try{
            if (meta.getGroupId() != null) {
                group = groupRepo.find("gid", meta.getGroupId()).firstResult();
            }
        } catch (Exception e) {
            log.error("error parsing metadata json {}", e. getMessage());
            return new LakeHttpResponse(400, "Bad Request", null);
        }

        // check chunk and save to backend
        if (cid == null || cid.isEmpty() || cid.isBlank() || cid.equals("null")) {
            cid = fs.create(meta.getName(), meta.getLength(), is);
            return new LakeHttpResponse(200, "OK", cid);
        }
        else {
            if (!fs.insertChunk(cid, is)) {
                return new LakeHttpResponse(500, "Internal error", null);
            }
        }

        // save a new object to metadata RDBMS
        LakeObject object = new LakeObject();
        object.setCid(cid);
        Long now = new Date().getTime();
        object.setCreateTime(now);
        object.setAccessTime(now);
        object.setParentId(0L);
//        repo.persist(object);
        log.info("POST: object metadata saved cid={}", cid);
        return new LakeHttpResponse(200, "OK", cid);
    }
}
