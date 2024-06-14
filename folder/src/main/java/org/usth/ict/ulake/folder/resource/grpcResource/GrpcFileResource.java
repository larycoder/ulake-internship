package org.usth.ict.ulake.folder.resource.grpcResource;

import io.grpc.stub.StreamObserver;
import io.quarkus.example.*;
import io.quarkus.grpc.GrpcService;
import io.quarkus.security.Authenticated;
import io.smallrye.common.annotation.Blocking;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.misc.AclUtil;
import org.usth.ict.ulake.common.misc.GrpcAclUtil;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.log.LogModel;
import org.usth.ict.ulake.common.model.user.User;
import org.usth.ict.ulake.common.service.LogService;
import org.usth.ict.ulake.folder.model.UserFile;
import org.usth.ict.ulake.folder.persistence.FileGrpcRepository;
import org.usth.ict.ulake.folder.persistence.FileRepository;
import org.usth.ict.ulake.folder.persistence.FolderGrpcRepository;
import org.usth.ict.ulake.folder.persistence.FolderRepository;
import org.usth.ict.ulake.folder.resource.FileResource;

@GrpcService
@Blocking
public class GrpcFileResource extends FileGrpcServiceGrpc.FileGrpcServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(GrpcFileResource.class);

    @Inject
    FileRepository repo;

    @Inject
    FolderRepository folderRepo;

    @Inject
    LakeHttpResponse<Object> respObject;

    @Inject
    GrpcAclUtil acl;

    @Inject
    @RestClient
    LogService logService;

    @Inject
    JsonWebToken jwt;

    @Override
    public void test(io.quarkus.example.UploadFileRequest request, io.grpc.stub.StreamObserver<io.quarkus.example.UploadFileResponse> responseObserver) {
        responseObserver.onNext(io.quarkus.example.UploadFileResponse.newBuilder().setCode(200).setMessage("OK").setToken("true").build());
        responseObserver.onCompleted();
    }

    @Override
    @POST
    @Path("/file")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a new file info")
//    @RolesAllowed({ "User", "Admin" })
    public void uploadFile(UploadFileRequest request, StreamObserver<UploadFileResponse> responseObserver) {
        String bearer = request.getBearer();
        var output = request.getOutput();
        UserFile userFile = new UserFile();
        userFile.name = output.getName();
        userFile.size = output.getSize();
        userFile.mime = output.getMime();
        userFile.ownerId = output.getOwnerId();
        userFile.cid = output.getCid();
        userFile.creationTime = output.getCreationTime();
        userFile.parent = null;

        LakeHttpResponse response = getResp(bearer, userFile);

        responseObserver.onNext(UploadFileResponse.newBuilder().setCode(response.getCode()).setMessage("OK").setToken(response.getResp().toString()).build());
        responseObserver.onCompleted();
    }

    public LakeHttpResponse getResp(String bearer, UserFile entity) {
        var permit = PermissionModel.WRITE;     // <-- permit
        var parentPermit = PermissionModel.ADD; // <-- permit

//        if (!acl.verify(io.quarkus.example.FileType.FILE, null, entity.ownerId, permit))
//            return new LakeHttpResponse<>(403, "Add file not allow ed", null);

        if (entity.parent != null && entity.parent.id != null) {
            var parent = folderRepo.findById(entity.parent.id);
            if (parent == null)
                return new LakeHttpResponse<>(403, "Parent folder is not existed", null);

            if (!acl.verify(FileType.FOLDER, parent.id, parent.ownerId, parentPermit))
                return new LakeHttpResponse<>(403, "Add file not allowed", null);
            entity.parent = parent;
        }

        repo.persist(entity);
        logService.post(bearer, new LogModel("Add", "Create file info for id " + entity.id + ", cid " + entity.cid + ", name " + entity.name));
        return new LakeHttpResponse<>(200, null, entity.cid + " " + entity.id + " " + entity.mime + " " + entity.name + " " + entity.ownerId + " " + entity.size + " " + entity.creationTime + " " + entity.parent);
//        return new LakeHttpResponse<>(200, null, jwt.getClaim(Claims.sub));
    }

}
