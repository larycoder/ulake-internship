package org.usth.ict.ulake.acl.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.usth.ict.ulake.common.model.PermissionModel;
import org.usth.ict.ulake.common.model.acl.macro.AclType;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(
    columnNames = {"type", "objectId", "userId", "permission"})
})
public class AclModel {
    @Id
    @Schema(description = "Marked ID for permission, must not included in query")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Enumerated(EnumType.STRING)
    @Schema(description = "User and file type")
    public AclType type;

    @Schema(description = "Object owner")
    public Long ownerId;

    @Schema(description = "File ID from file management")
    public Long objectId;

    @Schema(description = "User ID from user management")
    public Long userId;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Permission of object for corresponding user")
    public PermissionModel permission;

    public AclModel() {}
}
