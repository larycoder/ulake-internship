package org.usth.ict.ulake.folder.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * A file is mapped to an object in lake storage
 */
@Entity
public class UserFile extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;

    @Schema(description = "Corresponding cid of the lake core's object")
    public String cid;

    @Schema(description = "File name")
    public String name;

    @Schema(description = "File size")
    public Long size;

    @Schema(description = "File MIME. Set automatically by this service")
    public String mime;

    @Schema(description = "Id of the owner user")
    public Long ownerId;
}
