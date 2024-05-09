package org.usth.ict.ulake.table.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class TableColumnModel {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;

    @Schema(description = "Indicates if this column is used for grouping")
    public Boolean groupBy;

    @ManyToOne
    @JsonBackReference
    @JoinColumn
    @Schema(description = "Parent table")
    public TableModel table;

    @Schema(description = "Name of the column")
    public String columnName;

    @Schema(description = "Data type of the column. int/float/string/date/bool")
    public String dataType;
}
