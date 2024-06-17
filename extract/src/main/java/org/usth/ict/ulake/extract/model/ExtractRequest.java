package org.usth.ict.ulake.extract.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

// represent an extraction request
@Entity
public class ExtractRequest {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;

    @Schema(description = "Id of the requested user")
    public Long userId;

    @Schema(description = "Id of the compressed file for extraction")
    public Long fileId;

    @Schema(description = "Id of the target folder for extraction")
    public Long folderId;

    @Schema(description = "Requested time")
    public Long timestamp;

    @Schema(description = "Finished time")
    public Long finishedTime;
}
