package org.usth.ict.ulake.acl.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

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
