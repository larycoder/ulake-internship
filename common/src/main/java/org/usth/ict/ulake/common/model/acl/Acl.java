package org.usth.ict.ulake.common.model.acl;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.usth.ict.ulake.common.model.PermissionModel;

public class Acl {
    @Schema(description = "Object ID from file management")
    public Long objectId;

    @Schema(description = "User for permission")
    public Long userId;

    @Schema(description = "Group IDs for permission")
    public List<Long> groupIds;

    @Schema(description = "Permission of object for corresponding user")
    public PermissionModel permission;

    public Acl() {}
}
