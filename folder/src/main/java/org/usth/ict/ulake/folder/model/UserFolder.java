package org.usth.ict.ulake.folder.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * A directory is mapped to a group in lake
 * Root directory can be mapped to a dataset
 */
@Entity
public class UserFolder extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;

    @Schema(description = "Corresponding to the core Object Group id")
    public Long coreGroupId;

    @Schema(description = "Folder name")
    public String name;
    public Long ownerId;
}
