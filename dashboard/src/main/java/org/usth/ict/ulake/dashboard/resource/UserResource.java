package org.usth.ict.ulake.dashboard.resource;

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
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.user.AuthModel;
import org.usth.ict.ulake.common.service.UserService;

@Path("/user")
@Tag(name = "User")
public class UserResource {

    @Inject
    @RestClient
    UserService userSvc;

    @Inject
    LakeHttpResponse resp;

    @POST
    @Path("/login")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Login to dashboard")
    public Response login(AuthModel auth) {
        var authResp = userSvc.getToken(auth);
        return resp.build(
                   authResp.getCode(),
                   authResp.getMsg(),
                   authResp.getResp());
    }
}
