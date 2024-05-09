package org.usth.ict.ulake.folder.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

/**
 * Represent a directory
 */
@Entity
public class UserFolder extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "Folder identify")
    public Long id;

    @Schema(description = "Folder name")
    public String name;

    @Schema(description = "Folder owner")
    public Long ownerId;

    @Schema(description = "Creation time")
    public Long creationTime;
    
    @ManyToOne
    @JsonBackReference
    @JoinColumn
    @Schema(description = "Parent folder")
    public UserFolder parent;

    @JsonManagedReference
    @OneToMany(mappedBy = "parent")
    @Schema(description = "List of sub-folder")
    public List<UserFolder> subFolders;

    @JsonManagedReference
    @OneToMany(mappedBy = "parent")
    @Schema(description = "List of folder files")
    public List<UserFile> files;
}
