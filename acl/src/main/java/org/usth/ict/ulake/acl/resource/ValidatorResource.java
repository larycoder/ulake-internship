package org.usth.ict.ulake.acl.resource;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.usth.ict.ulake.acl.model.UserFileAcl;
import org.usth.ict.ulake.acl.model.UserFolderAcl;
import org.usth.ict.ulake.acl.persistence.GroupFileAclRepo;
import org.usth.ict.ulake.acl.persistence.GroupFolderAclRepo;
import org.usth.ict.ulake.acl.persistence.UserFileAclRepo;
import org.usth.ict.ulake.acl.persistence.UserFolderAclRepo;
import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.model.acl.Acl;

@Path("/acl/validation")
@Produces(MediaType.APPLICATION_JSON)
public class ValidatorResource {
    @Inject
    LakeHttpResponse resp;

    @Inject
    UserFileAclRepo userFileRepo;

    @Inject
    UserFolderAclRepo userFolderRepo;

    @Inject
    GroupFileAclRepo groupFileRepo;

    @Inject
    GroupFolderAclRepo groupFolderRepo;

    @POST
    @Path("/file")
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "assert permission of file")
    @APIResponses({
        @APIResponse(name = "400", responseCode = "400", description = "Invalid ACL passing"),
        @APIResponse(name = "200", responseCode = "200", description = "OK"),
    })
    public Response validateFile(Acl acl) {
        var user = new UserFileAcl();
        user.userId = acl.ownerId;
        user.fileId = acl.objectId;
        user.permission = acl.permission;
        if (userFileRepo.hasAcl(user))
            return resp.build(200, null, true);

        if (!groupFileRepo.listAcl(acl).isEmpty())
            return resp.build(200, null, true);

        return resp.build(200, null, false);
    }

    @POST
    @Path("/folder")
    @RolesAllowed({"User", "Admin"})
    @Operation(summary = "assert permission of folder")
    @APIResponses({
        @APIResponse(name = "400", responseCode = "400", description = "Invalid ACL passing"),
        @APIResponse(name = "200", responseCode = "200", description = "OK"),
    })
    public Response validateFolder(Acl acl) {
        var user = new UserFolderAcl();
        user.userId = acl.ownerId;
        user.folderId = acl.objectId;
        user.permission = acl.permission;
        if (userFolderRepo.hasAcl(user))
            return resp.build(200, null, true);

        if (!groupFolderRepo.listAcl(acl).isEmpty())
            return resp.build(200, null, true);

        return resp.build(200, null, false);
    }
}
