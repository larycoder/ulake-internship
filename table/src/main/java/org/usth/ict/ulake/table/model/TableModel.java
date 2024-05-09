package org.usth.ict.ulake.table.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

// represent a table
@Entity
public class TableModel {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;

    @Schema(description = "Name of the table")
    public String name;

    @Schema(description = "Id of the owner")
    public Long ownerId;

    @Schema(description = "Creation time")
    public Long creationTime;

    @Schema(description = "Original format of the uploaded table")
    public String format;
}
