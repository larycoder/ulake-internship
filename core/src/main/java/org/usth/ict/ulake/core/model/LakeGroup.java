package org.usth.ict.ulake.core.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.util.List;

@Entity
public class LakeGroup  extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;
    public String name;
    public String extraJson;
    public String tags;

    @OneToMany(mappedBy = "group")
    public List<LakeObject> objects;

    @ManyToOne
    @JoinColumn
    public LakeDataset dataset;

    public LakeGroup() { }
}
