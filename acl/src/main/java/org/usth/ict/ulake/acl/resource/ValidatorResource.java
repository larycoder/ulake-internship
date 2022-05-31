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
import org.usth.ict.ulake.acl.model.FileAcl;
import org.usth.ict.ulake.acl.model.FolderAcl;
import org.usth.ict.ulake.acl.model.GroupFileAcl;
import org.usth.ict.ulake.acl.model.GroupFolderAcl;
import org.usth.ict.ulake.acl.model.UserFileAcl;
import org.usth.ict.ulake.acl.model.UserFolderAcl;
import org.usth.ict.ulake.acl.persistence.GroupFileAclRepo;
import org.usth.ict.ulake.acl.persistence.GroupFolderAclRepo;
import org.usth.ict.ulake.acl.persistence.UserFileAclRepo;
import org.usth.ict.ulake.acl.persistence.UserFolderAclRepo;
import org.usth.ict.ulake.common.model.LakeHttpResponse;

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
    public Response validate(FileAcl acl) {
        var user = new UserFileAcl();
        user.userId = acl.onwerId;
        user.fileId = acl.fileId;
        user.permission = acl.permission;
        if (userFileRepo.hasAcl(user))
            return resp.build(200, null, true);

        var group = new GroupFileAcl();
        group.groupId = acl.onwerId;
        group.fileId = acl.fileId;
        group.permission = acl.permission;
        if (groupFileRepo.hasAcl(group))
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
    public Response validate(FolderAcl acl) {
        var user = new UserFolderAcl();
        user.userId = acl.onwerId;
        user.folderId = acl.folderId;
        user.permission = acl.permission;
        if (userFolderRepo.hasAcl(user))
            return resp.build(200, null, true);

        var group = new GroupFolderAcl();
        group.groupId = acl.onwerId;
        group.folderId = acl.folderId;
        group.permission = acl.permission;
        if (groupFolderRepo.hasAcl(group))
            return resp.build(200, null, true);

        return resp.build(200, null, false);
    }
}
