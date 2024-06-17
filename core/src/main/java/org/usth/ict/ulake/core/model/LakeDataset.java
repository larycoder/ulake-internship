package org.usth.ict.ulake.core.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class LakeDataset extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer id;
    public String name;
    public String description;
    public String source;
    public String licence;
    public String tags;

    @OneToMany(mappedBy = "dataset")
    public List<LakeGroup> groups;

    public LakeDataset() {
    }

}
