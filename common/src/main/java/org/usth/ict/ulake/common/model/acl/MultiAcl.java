package org.usth.ict.ulake.common.model.acl;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.usth.ict.ulake.common.model.PermissionModel;
import org.usth.ict.ulake.common.model.acl.macro.FileType;

public class MultiAcl {
    @Schema(description = "Object ID from file management")
    public Long objectId;

    @Schema(description = "User for permission")
    public Long userId;

    @Schema(description = "Object type for file management")
    public FileType type;

    @Schema(description = "Permissions of object for corresponding user")
    public List<PermissionModel> permissions;

    public MultiAcl() {}
}
