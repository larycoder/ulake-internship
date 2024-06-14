package org.usth.ict.ulake.dashboard.resource.grpcResouce;

import io.quarkus.example.LoginRequest;
import io.quarkus.example.UserGrpcServiceGrpc;
import io.quarkus.grpc.GrpcClient;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.log.LogModel;
import org.usth.ict.ulake.common.model.user.AuthModel;

import jakarta.ws.rs.core.Response;

@Path("/grpc")
public class GrpcUserClientResource {
    @GrpcClient
    UserGrpcServiceGrpc.UserGrpcServiceBlockingStub grpcClient;

    @Inject
    LakeHttpResponse resp;

    @POST
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/test")
    public Response hello(AuthModel auth) {
        var code =  grpcClient.sayHello(LoginRequest.newBuilder().setUserName(auth.userName).setPassword(auth.password).build()).getCode();
        var message = grpcClient.sayHello(LoginRequest.newBuilder().setUserName(auth.userName).setPassword(auth.password).build()).getMessage();
        var token = grpcClient.sayHello(LoginRequest.newBuilder().setUserName(auth.userName).setPassword(auth.password).build()).getToken();
        return resp.build(code, message, token);
    }

    @POST
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/grpc-login")
    public Response loginGrpc(AuthModel auth) {
        var response = grpcClient.loginGrpc(LoginRequest.newBuilder().setUserName(auth.userName).setPassword(auth.password).build());
        return resp.build(
                response.getCode(),
                response.getMessage(),
                response.getToken()
        );
    }

}
