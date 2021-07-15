package org.usth.ict.ulake.core.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class GroupObject {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String cid;
    private String name;
    private String description;
    private String source;
    private String licence;
    private String tags;

    @OneToMany(mappedBy = "group")
    private List<StorageObject> objects;

    public GroupObject(String name) {
        this.name = name;
    }

    public GroupObject() {
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
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

    public List<StorageObject> getObjects() {
        return objects;
    }

    public void setObjects(List<StorageObject> objects) {
        this.objects = objects;
    }
}
