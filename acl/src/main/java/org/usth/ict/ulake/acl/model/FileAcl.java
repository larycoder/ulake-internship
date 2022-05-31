package org.usth.ict.ulake.acl.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.usth.ict.ulake.common.model.PermissionModel;

public class FileAcl {
    @Schema(description = "File ID from file management")
    public Long fileId;

    @Schema(description = "Owner ID from user management")
    public Long onwerId;

    @Schema(description = "Permission of object for corresponding user")
    public PermissionModel permission;

    public FileAcl() {}
}
