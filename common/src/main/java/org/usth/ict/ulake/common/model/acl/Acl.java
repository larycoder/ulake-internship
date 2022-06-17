package org.usth.ict.ulake.common.model.acl;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.usth.ict.ulake.common.model.PermissionModel;

public class Acl {
    @Schema(description = "Object ID from file management")
    public Long objectId;

    @Schema(description = "Owner ID from user management")
    public Long onwerId;

    @Schema(description = "Group IDs from user management")
    public List<Long> groupIds;

    @Schema(description = "Permission of object for corresponding user")
    public PermissionModel permission;

    public Acl() {}
}
