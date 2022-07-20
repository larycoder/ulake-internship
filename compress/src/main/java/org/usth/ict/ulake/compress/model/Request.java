package org.usth.ict.ulake.compress.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

// represent a compression request
@Entity
public class Request {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;

    @Schema(description = "Id of the requested user")
    public Long userId;

    // @JsonBackReference("department")
    @ManyToOne
    @JoinColumn
    public User department;

    @Schema(description = "Requested time")
    public Long timestamp;

    @Schema(description = "Finished time")
    public Long finishedTime;
}
