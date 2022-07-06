package org.usth.ict.ulake.extract.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

// represent an extracted info in request
@Entity
public class ExtractResultFile {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;

    @Schema(description = "Id of the request")
    public Long requestId;

    @Schema(description = "Extracted result file id")
    public Long fileId;
}
