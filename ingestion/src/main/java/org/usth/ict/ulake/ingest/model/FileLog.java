package org.usth.ict.ulake.ingest.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Entity
public class FileLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Schema(description = "File id in lake storage")
    public Long fileId;

    @Schema(description = "Start time of uploading file to storage")
    public Long uploadTime;

    @Schema(description = "File crawl status")
    public Boolean status;

    @Column(length = 500)
    @Schema(description = "Meta relating to crawl file (max: 500B)")
    public String meta;

    @ManyToOne
    @JoinColumn
    @Schema(description = "Process which create this file")
    public ProcessLog process;

    public FileLog() {}

    /**
     * Helper method to truncate meta before pushing to database.
     * */
    public void setMeta(String meta) {
        if (meta.length() > 500)
            meta = meta.substring(0, 500);
        this.meta = meta;
    }
}
