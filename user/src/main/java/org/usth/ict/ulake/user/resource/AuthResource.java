package org.usth.ict.ulake.user.resource;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.common.misc.RandomString;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.user.model.LoginCredential;
import org.usth.ict.ulake.user.model.User;
import org.usth.ict.ulake.user.persistence.UserRepository;
import org.wildfly.security.password.Password;
import org.wildfly.security.password.PasswordFactory;
import org.wildfly.security.password.WildFlyElytronPasswordProvider;
import org.wildfly.security.password.interfaces.BCryptPassword;
import org.wildfly.security.password.util.ModularCrypt;

import io.smallrye.jwt.build.Jwt;

@Path("/auth")
@Tag(name = "Authentication")
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
    @Operation(summary = "Test")
    public String test(@Context SecurityContext ctx) {
        return getResponseString(ctx);
    }

    @GET
    @Path("logout")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Logout, clear all currently provided tokens")
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

    private Response privLogin(LoginCredential cred, boolean skipPassword) {
        User user = repo.checkLogin(cred, skipPassword);
        if (user == null) {
            return response.build(401);
        }
        user.accessToken =
                Jwt.issuer("https://sontg.net/issuer")
                        .upn(user.email)
                        .expiresIn(tokenExpire)
                        .groups(new HashSet<>(Arrays.asList("User", "Admin")))
                        .claim(Claims.auth_time.name(), new Date().getTime())
                        .claim(Claims.sub.name(), String.valueOf(user.id))
                        .sign();

        // save access token, so we can detect abnormal cases
        // e.g. attacker uses an old accessToken
        // while the access & refresh tokens were legally refreshed.
        user.refreshToken = randomString.nextString();
        user.refreshTokenExpire = refreshTokenExpire;
        repo.persist(user);
        Map.Entry<String, String> cookie = new AbstractMap.SimpleEntry<String, String>("Set-Cookie", "jwt=" + user.refreshToken + "; SameSite=strict");
        return response.build(200, "", user.accessToken, cookie);
    }

    // authentication user with u/p
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
    public Response login(@RequestBody(description = "Username and password (not hashed) to authenticate") LoginCredential cred) {
        return privLogin(cred, false);
    }

    // reauthentication user with refresh token
    @POST
    @Path("/refresh")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    @Operation(summary = "Get a new JWT using a refresh token")
    @APIResponses({
            @APIResponse(name = "200", responseCode = "200", description = "Reauthentication successful. JWT is in .resp, refresh token is in .msg"),
            @APIResponse(name = "401", responseCode = "401", description = "Reauthentication error. Refresh token invalid")
    })
    public Response refresh(@RequestBody(description = "Refresh token to reauthenticate")LoginCredential cred) {
        User user = repo.checkRefreshLogin(cred);
        if (user == null) {
            return response.build(401, "Incorrect refresh token");
        }
        cred.setUserName(user.userName);
        return privLogin(cred, true);
    }

    public boolean verifyPassword(String bCryptPasswordHash, String passwordToVerify) throws Exception {
        WildFlyElytronPasswordProvider provider = new WildFlyElytronPasswordProvider();
        PasswordFactory passwordFactory = PasswordFactory.getInstance(BCryptPassword.ALGORITHM_BCRYPT, provider);
        Password userPasswordDecoded = ModularCrypt.decode(bCryptPasswordHash);
        Password userPasswordRestored = passwordFactory.translate(userPasswordDecoded);
        return passwordFactory.verify(userPasswordRestored, passwordToVerify.toCharArray());
    }
}
