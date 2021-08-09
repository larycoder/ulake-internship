package org.usth.ict.ulake.api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.util.List;

@Entity
public class Institution  extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;
    public String name;

    @JsonManagedReference
    @OneToMany(mappedBy = "institution")
    public List<Department> departments;

    public Institution() { }
}
