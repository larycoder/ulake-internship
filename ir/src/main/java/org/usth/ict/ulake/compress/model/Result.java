package org.usth.ict.ulake.compress.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

// represent a compression result
@Entity
public class Result {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;

    @Schema(description = "Compression request id")
    public String req;

    @Schema(description = "Location on disk to store cached compressed data")
    public String url;

    @Schema(description = "Id of the requested user")
    public Long ownerId;

    @Schema(description = "Creation time")
    public Long creationTime;

    @Schema(description = "Original format of the uploaded table")
    public String format;
}
