package org.usth.ict.ulake.core.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class LakeGroup {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;
    private String name;
    private String extraJson;
    private String tags;

    @OneToMany(mappedBy = "group")
    private List<LakeObject> objects;

    @ManyToOne
    @JoinColumn
    private LakeDataset dataset;

    public LakeGroup() { }

    public LakeGroup(String name) {
        this.name = name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getExtraJson() {
        return extraJson;
    }

    public void setExtraJson(String extraJson) {
        this.extraJson = extraJson;
    }

    public List<LakeObject> getObjects() {
        return objects;
    }

    public void setObjects(List<LakeObject> objects) {
        this.objects = objects;
    }

    public LakeDataset getDataset() {
        return dataset;
    }

    public void setDataset(LakeDataset dataset) {
        this.dataset = dataset;
    }
}
