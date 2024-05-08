package org.usth.ict.ulake.acl.resource;

import java.util.ArrayList;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
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
import org.usth.ict.ulake.acl.persistence.AclRepo;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.acl.MultiAcl;
import org.usth.ict.ulake.common.model.acl.macro.AclType;
import org.usth.ict.ulake.common.model.acl.macro.FileType;
import org.usth.ict.ulake.common.model.acl.macro.UserType;
import org.usth.ict.ulake.common.service.FileService;
import org.usth.ict.ulake.common.service.exception.LakeServiceException;
import org.usth.ict.ulake.common.service.exception.LakeServiceForbiddenException;
import org.usth.ict.ulake.common.service.exception.LakeServiceNotFoundException;

@Path("/acl")
@Produces(MediaType.APPLICATION_JSON)
public class AclResource {
    private static final Logger log = LoggerFactory.getLogger(AclResource.class);

    @Inject
    LakeHttpResponse<Object> response;

    @Inject
    AclRepo repo;

    @Inject
    @RestClient
    FileService svc;

    @Inject
    JsonWebToken jwt;

    @GET
    @RolesAllowed({"User","Admin"})
    @Operation(summary = "List all ACL with permission for current user")
    public Response list() {
        Long userId = Long.parseLong(jwt.getClaim(Claims.sub));
        return getByActor(UserType.user, userId);
    }

    @GET
    @Path("/all")
    @RolesAllowed({"Admin"})
    @Operation(summary = "list all ACL without permission")
    public Response all() {
        return response.build(200, null, repo.listAcl());
    }

    @GET
    @Path("/{user}/{file}/{id}")
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "list acl of object")
    public Response get(
        @PathParam("user") UserType user,
        @PathParam("file") FileType file,
        @PathParam("id") Long id) {

        AclType type = AclType.valueOf(user.label + file.label);
        Long userId = Long.parseLong(jwt.getClaim(Claims.sub));
        String hql = "objectId = ?1 and type = ?2";

        var oneAcl = repo.find(hql, id, type).firstResult();
        if (oneAcl == null)
            return response.build(200, null, new ArrayList<MultiAcl>());

        Long ownerId = oneAcl.ownerId;
        if (!jwt.getGroups().contains("Admin") && userId.longValue() != ownerId.longValue())
            return response.build(403, "Admin and owner only");
        else
            return response.build(200, null, repo.listMultiAcl(type, id));
    }

    @GET
    @Path("/{user}/{id}")
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "List acl of an user/group")
    public Response getByActor(
        @PathParam("user") UserType actor,
        @PathParam("id") Long id) {
        // should we check for ownership here?
        Long userId = Long.parseLong(jwt.getClaim(Claims.sub));
        if (!jwt.getGroups().contains("Admin") && userId.longValue() != id.longValue())
            return response.build(403, "Admin and owner only");
        return response.build(200, null, repo.listActorMultiAcl(actor, id));
    }


    @POST
    @Path("/{user}/{file}/{id}")
    @Transactional
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "synchronize acl of object")
    public Response post(
        @HeaderParam("Authorization") String bearer,
        @PathParam("user") UserType user,
        @PathParam("file") FileType file,
        @PathParam("id") Long id,
        MultiAcl acl) {

        AclType type = AclType.valueOf(user.label + file.label);
        Long userId = Long.parseLong(jwt.getClaim(Claims.sub));

        Long ownerId;
        try {
            if (file == FileType.file)
                ownerId = svc.fileInfo(id, bearer).getResp().ownerId;
            else
                ownerId = svc.folderInfo(bearer, id).getResp().ownerId;
        } catch (LakeServiceForbiddenException e) {
            return response.build(403, "File info retrieval is forbidden");
        } catch (LakeServiceNotFoundException e) {
            return response.build(404, "File info not found");
        } catch (LakeServiceException e) {
            e.printStackTrace();
            return response.build(500, "Something wrong in file info retrieval");
        }

        acl.objectId = id;
        if (!jwt.getGroups().contains("Admin") && userId.longValue() != ownerId.longValue())
            return response.build(403, "Admin and owner only");
        else
            return response.build(200, null, repo.sync(type, ownerId, acl));
    }
}
