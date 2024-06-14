package org.usth.ict.ulake.acl.resource.grpcResource;

import io.quarkus.example.AclGrpcServiceGrpc;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.Blocking;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.usth.ict.ulake.acl.persistence.AclRepo;
import org.usth.ict.ulake.common.model.LakeHttpResponse;

@GrpcService
public class GrpcAclResource extends AclGrpcServiceGrpc.AclGrpcServiceImplBase {
    @Inject
    LakeHttpResponse<Object> resp;

    @Inject
    AclRepo repo;

    @Override
    @Path("/acl/validation/{fileType}")
    @POST
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "assert permission of file")
    @APIResponses({
            @APIResponse(name = "400", responseCode = "400", description = "Invalid ACL passing"),
            @APIResponse(name = "200", responseCode = "200", description = "OK"),
    })
    @Produces(MediaType.APPLICATION_JSON)
    public void validate(@PathParam("fileType") io.quarkus.example.ValidateRequest request,
                             io.grpc.stub.StreamObserver<io.quarkus.example.ValidateResponse> responseObserver) {
        var type = request.getFileType();
        var acl = request.getFile();
        if (!repo.findAcl(type, acl).isEmpty()){
            responseObserver.onNext(io.quarkus.example.ValidateResponse.newBuilder().setCode(200).setMessage("OK").setToken("true").build());
            responseObserver.onCompleted();
        }
        else{
            responseObserver.onNext(io.quarkus.example.ValidateResponse.newBuilder().setCode(200).setMessage("OK").setToken("false").build());
            responseObserver.onCompleted();
        }
    }
}
