package org.usth.ict.ulake.acl.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Composite key of ACL
 * */
class AclPK implements Serializable {
    public Long objectId;
    public Long userId;
    public Integer permission;
    public String isFolder;
    public String isGroup;
}

/**
 * ACL model to link user to file with corresponding permission.
 * */
@Entity
@IdClass(AclPK.class)
public class AclModel {
    @Id
    @Schema(description = "File / Folder id from file management service")
    private Long objectId;

    @Schema(description = "Flag to mark object is folder")
    @Column(length = 1)
    private String isFolder = "0";

    @Id
    @Schema(description = "User / Group user id from user management service")
    private Long userId;

    @Schema(description = "Flag to mark user is group user")
    @Column(length = 1)
    private String isGroup = "0";

    @Schema(description = "Permission of object for corresponding user")
    private Integer permission;

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getIsFolder() {
        return isFolder;
    }

    public void setIsFolder(String isFolder) {
        this.isFolder = isFolder;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(String isGroup) {
        this.isGroup = isGroup;
    }

    public Integer getPermission() {
        return permission;
    }

    public void setPermission(Integer permission) {
        this.permission = permission;
    }
}
