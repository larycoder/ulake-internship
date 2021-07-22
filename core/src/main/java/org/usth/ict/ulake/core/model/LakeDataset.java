package org.usth.ict.ulake.core.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class LakeDataset {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private String description;
    private String source;
    private String licence;
    private String tags;

    @OneToMany(mappedBy = "dataset")
    private List<LakeGroup> groups;

    public LakeDataset() {
    }

    public LakeDataset(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<LakeGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<LakeGroup> groups) {
        this.groups = groups;
    }
}
