package org.usth.ict.ulake.lcc.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Entity
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Schema(description = "Data file corresponding to patient")
    public Long fileId;

    @Schema(description = "Patient name")
    public String name;

    @Schema(description = "Patient file type")
    public String modality;

    @Schema(description = "Patient gender")
    public String gender;

    @Schema(description = "Patient study date")
    public Long studyDate;
}
