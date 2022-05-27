package org.usth.ict.ulake.acl.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

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

    @Schema(description = "Flag to mark object is folder")
    @Column(length = 1)
    public String isFolder = "0";

    @Schema(description = "User ID from user management")
    public Long userId;

    @Schema(description = "Permission of object for corresponding user")
    public Integer permission;

    public FolderAcl() {}
}
