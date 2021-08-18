package org.usth.ict.ulake.user.resource;

import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.misc.RandomString;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.user.model.LoginCredential;
import org.usth.ict.ulake.user.model.User;
import org.usth.ict.ulake.user.persistence.UserRepository;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

@Path("/auth")
@RequestScoped
public class AuthResource {
    private static final Logger log = LoggerFactory.getLogger(AuthResource.class);

    @Inject
    LakeHttpResponse response;

    @Inject
    UserRepository repo;

    @Inject
    RandomString randomString;

    @ConfigProperty(name = "ulake.jwt.accesstoken.expire")
    long tokenExpire;

    @ConfigProperty(name = "ulake.jwt.refreshtoken.expire")
    long refreshTokenExpire;

    @Inject
    JsonWebToken jwt;

    @GET
    @PermitAll
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@Context SecurityContext ctx) {
        return getResponseString(ctx);
    }

    @GET
    @Path("logout")
    @RolesAllowed({ "User", "Admin" })
    @Produces(MediaType.TEXT_PLAIN)
    public String logout(@Context SecurityContext ctx) {
        return getResponseString(ctx) + ", birthdate: " + jwt.getClaim("birthdate").toString();
    }

    private String getResponseString(SecurityContext ctx) {
        String name;
        if (ctx.getUserPrincipal() == null) {
            name = "anonymous";
        } else if (!ctx.getUserPrincipal().getName().equals(jwt.getName())) {
            throw new InternalServerErrorException("Principal and JsonWebToken names do not match");
        } else {
            name = ctx.getUserPrincipal().getName();
        }
        return String.format("hello + %s,"
                        + " isHttps: %s,"
                        + " authScheme: %s,"
                        + " hasJWT: %s",
                name, ctx.isSecure(), ctx.getAuthenticationScheme(), hasJwt());
    }

    private boolean hasJwt() {
        return jwt.getClaimNames() != null;
    }

    // authentication user with u/p
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginCredential cred) {
        User user = repo.checkLogin(cred);
        if (user == null) {
            return response.build(401);
        }
        user.accessToken =
                Jwt.issuer("https://sontg.net/issuer")
                        .upn(user.email)
                        .expiresIn(tokenExpire)
                        .groups(new HashSet<>(Arrays.asList("User", "Admin")))
                        .claim(Claims.auth_time.name(), new Date().getTime())
                        .sign();

        // save access token, so we can detect abnormal cases
        // e.g. attacker uses an old accessToken
        // while the access & refresh tokens were legally refreshed.
        user.refreshToken = randomString.nextString();
        user.refreshTokenExpire = refreshTokenExpire;
        return response.build(200, user.refreshToken, user.accessToken);
    }
}
