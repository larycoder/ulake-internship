package org.usth.ict.ulake.ingest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Entity
public class FileLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Schema(description = "File id in lake storage")
    public Long fileId;

    @Schema(description = "File size")
    public Long size;

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
    public CrawlRequest process;

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
