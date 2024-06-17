package org.usth.ict.ulake.compress.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

// represent a file info in the compression request
@Entity
public class CompressRequestFile {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;

    @Schema(description = "Id of the request")
    public Long requestId;

    @Schema(description = "Requested file id")
    public Long fileId;
}
