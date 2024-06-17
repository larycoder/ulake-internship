package org.usth.ict.ulake.extract.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

// represent an extraction result
@Entity
public class ExtractResult {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;

    @Schema(description = "Extraction request id")
    public Long requestId;

    @Schema(description = "CID in lake core containing compressed data")
    public String url;

    @Schema(description = "Id of the requested user")
    public Long ownerId;

    @Schema(description = "How many files have been extracted")
    public Long progress;

    @Schema(description = "How many files in total should be extracted")
    public Long totalFiles;
}
