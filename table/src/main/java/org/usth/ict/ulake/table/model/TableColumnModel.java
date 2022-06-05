package org.usth.ict.ulake.table.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class TableColumnModel {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;

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
