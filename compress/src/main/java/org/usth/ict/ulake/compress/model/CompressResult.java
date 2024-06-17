package org.usth.ict.ulake.compress.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

// represent a compression result
@Entity
public class CompressResult {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;

    @Schema(description = "Compression request id")
    public Long requestId;

    @Schema(description = "Location on disk to store cached compressed data")
    public String url;

    @Schema(description = "Id of the requested user")
    public Long ownerId;

    @Schema(description = "How many files have been compressed into the big file")
    public Long progress;

    @Schema(description = "How many files in total should be compressed")
    public Long totalFiles;
}
