package org.usth.ict.ulake.compress.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

// represent a file info in the compression request
@Entity
public class RequestFile {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;

    @Schema(description = "Id of the request")
    public Long requestId;

    @Schema(description = "Requested file id")
    public Long fileId;
}
