package org.usth.ict.ulake.acl.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.usth.ict.ulake.common.model.PermissionModel;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(
    columnNames = {"folderId", "userId", "permission"})
})
public class FolderAcl {
    @Id
    @Schema(description = "Marked ID for permission, must not included in query")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Schema(description = "File ID from file management")
    public Long folderId;

    @Schema(description = "User ID from user management")
    public Long userId;

    @Schema(description = "Permission of object for corresponding user")
    public PermissionModel permission;

    public FolderAcl() {}
}
