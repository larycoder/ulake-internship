package org.usth.ict.ulake.ingest.model;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.usth.ict.ulake.ingest.utils.PolicyToStringConverter;

@Entity
public class UserConfigure {
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

    @Schema(description = "Created time of configure")
    public Long createdTime;

    @Schema(description = "Updated time of configure")
    public Long updatedTime;

    public UserConfigure() {}
}
