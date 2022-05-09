package org.usth.ict.ulake.core.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.util.List;

@Entity
public class LakeGroup  extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;
    public String name;
    public String gid;
    public String parentGid;
    public String extra;
    public String tags;

    @JsonBackReference
    @ManyToOne
    @JoinColumn
    public LakeGroup group;

    @JsonManagedReference
    @OneToMany(mappedBy = "group")
    public List<LakeObject> objects;

    @JsonManagedReference
    @OneToMany(mappedBy = "group")
    public List<LakeGroup> groups;

    @ManyToOne
    @JoinColumn
    public LakeDataset dataset;

    public LakeGroup() { }
}
