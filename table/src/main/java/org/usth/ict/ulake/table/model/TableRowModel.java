package org.usth.ict.ulake.table.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonBackReference;

// represents a row in a table
@Entity
public class TableRowModel {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn
    @Schema(description = "Parent table")
    public TableModel table;
}
