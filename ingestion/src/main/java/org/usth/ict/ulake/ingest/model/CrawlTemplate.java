package org.usth.ict.ulake.ingest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.usth.ict.ulake.ingest.utils.PolicyToStringConverter;

@Entity
public class CrawlTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    // TODO: missing JavaTypeDescriptor for custom Policy type
    @Lob
    @Convert(converter = PolicyToStringConverter.class)
    @Schema(description = "Template of crawl query")
    public Policy query;

    @Schema(description = "Owner who build query template")
    public Long ownerId;

    @Column(length = 2000)
    @Schema(description = "Explanation of crawl template")
    public String description;

    @Schema(description = "Created time of template")
    public Long createdTime;

    @Schema(description = "Updated time of template")
    public Long updatedTime;

    public CrawlTemplate() {}
}
