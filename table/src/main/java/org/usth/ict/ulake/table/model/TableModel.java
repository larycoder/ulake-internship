package org.usth.ict.ulake.table.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
}
