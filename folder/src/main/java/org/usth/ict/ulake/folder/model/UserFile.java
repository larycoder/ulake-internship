package org.usth.ict.ulake.folder.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

/**
 * A file is mapped to an object in lake storage
 */
@Entity
public class UserFile extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @Schema(description = "File creation time")
    public Long creationTime;

    @ManyToOne
    @JsonBackReference
    @JoinColumn
    @Schema(description = "Parent folder")
    public UserFolder parent;
}
