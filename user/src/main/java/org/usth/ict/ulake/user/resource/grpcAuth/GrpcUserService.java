package org.usth.ict.ulake.user.resource.grpcAuth;

import io.grpc.stub.StreamObserver;
import io.quarkus.example.UserGrpcServiceGrpc;
import io.quarkus.example.LoginRequest;
import io.quarkus.example.LoginResponse;
import io.quarkus.grpc.GrpcService;
import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.log.LogModel;
import org.usth.ict.ulake.common.model.user.AuthModel;
import org.usth.ict.ulake.common.service.LogService;
import org.usth.ict.ulake.user.model.LoginCredential;
import org.usth.ict.ulake.user.model.User;
import org.usth.ict.ulake.user.persistence.UserRepository;

import java.util.*;

@GrpcService
@jakarta.inject.Singleton
public class GrpcUserService extends UserGrpcServiceGrpc.UserGrpcServiceImplBase{
    private final static Logger log = LoggerFactory.getLogger(GrpcUserService.class);

    @Inject
    LakeHttpResponse response;

    @Inject
    UserRepository repo;

    @ConfigProperty(name = "ulake.jwt.accesstoken.expire")
    long tokenExpire;

    @ConfigProperty(name = "ulake.jwt.refreshtoken.expire")
    long refreshTokenExpire;

    @Inject
    JsonWebToken jwt;

    @Inject
    @RestClient
    LogService logService;

    @Override
    public void sayHello(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        responseObserver.onNext(LoginResponse.newBuilder().setCode(200).setMessage("Hello " + request.getUserName() + request.getPassword()).setToken("aaa").build());
        responseObserver.onCompleted();
    }

    @Override
    @POST
    @Transactional
    @Path("/login")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Authenticate user")
    @APIResponses({
            @APIResponse(name = "200", responseCode = "200", description = "Authentication successful. JWT is in .resp, refresh token is in .msg"),
            @APIResponse(name = "401", responseCode = "401", description = "Authentication error")
    })
    public void loginGrpc(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        LoginCredential cred = new LoginCredential();
        cred.setUserName(request.getUserName());
        cred.setPassword(request.getPassword());
        LakeHttpResponse resp = privLogin(cred, false);
        responseObserver.onNext(LoginResponse.newBuilder().setCode(resp.getCode()).setMessage(resp.getMsg()).setToken(resp.getResp().toString()).build());
        responseObserver.onCompleted();
    }

    private LakeHttpResponse privLogin(LoginCredential cred, boolean skipPassword) {
        User user = repo.checkLogin(cred, skipPassword);
        if (user == null || user.status == null || user.status == false) {
            return new LakeHttpResponse<>(401, "Authentication error", "");
        }

        List<String> groups;
        if (user.isAdmin != null && user.isAdmin.equals(true)) {
            groups = Arrays.asList("User", "Admin");
        } else {
            groups = Arrays.asList("User");
        }

        user.accessToken =
                Jwt.issuer("https://sontg.net/issuer")
                        .upn(user.email)
                        .expiresIn(tokenExpire)
                        .groups(new HashSet<>(groups))
                        .claim(Claims.auth_time.name(), new Date().getTime())
                        .claim(Claims.sub.name(), String.valueOf(user.id))
                        .sign();

        user.refreshToken = UUID.randomUUID().toString();
        user.refreshTokenExpire = refreshTokenExpire;
        repo.persist(user);
        Map.Entry<String, String> cookie = new AbstractMap.SimpleEntry<String, String>("Set-Cookie", "jwt=" + user.refreshToken + "; SameSite=strict");
        logService.post(user.accessToken, new LogModel("Auth", "Attempt to login " + cred.getUserName()));
        return new LakeHttpResponse<>(200, "", user.accessToken, cookie);
    }
}