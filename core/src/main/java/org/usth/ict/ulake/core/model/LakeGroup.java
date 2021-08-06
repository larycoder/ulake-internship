package org.usth.ict.ulake.core.model;

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
    public String extraJson;
    public String tags;

    @JsonManagedReference
    @OneToMany(mappedBy = "group")
    public List<LakeObject> objects;

    @ManyToOne
    @JoinColumn
    public LakeDataset dataset;

    public LakeGroup() { }
}
