package org.usth.ict.ulake.lcc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @Schema(description = "Detection process creation job time")
    public Long creationTime;

    @Schema(description = "Detection process start time")
    public Long startTime;

    @Schema(description = "Detection process end time")
    public Long endTime;

    @Schema(description = "Detection status")
    public Integer status;

    @Column(length = 1000)
    @Schema(description = "Detection message")
    public String message;

    @Lob
    @JsonIgnore
    @Schema(description = "Processed images")
    public String image;
}
