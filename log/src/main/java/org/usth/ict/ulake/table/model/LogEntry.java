package org.usth.ict.ulake.table.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

// represent a log entry
@Entity
public class LogEntry {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;

    @Schema(description = "Log time, in millisecs")
    public Long timestamp;

    @Schema(description = "Log for which user?")
    public Long ownerId;

    @Schema(description = "Log level (0=debug, 1=info, 2=warn, 3=error)")
    public Long level;

    @Schema(description = "Tag of the log")
    public String tag;

    @Schema(description = "Service name")
    public String service;

    @Schema(description = "Log entry contents")
    public String content;

}
