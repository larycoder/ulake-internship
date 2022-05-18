package org.usth.ict.ulake.core.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
public class LakeGroup  extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    public String name;
    public String gid;
    public String parentGid;
    public String extra;
    public String tags;

    @JsonManagedReference
    @OneToMany(mappedBy = "group")
    public List<LakeObject> objects;

    @ManyToOne
    @JoinColumn
    public LakeDataset dataset;

    public LakeGroup() { }
}
