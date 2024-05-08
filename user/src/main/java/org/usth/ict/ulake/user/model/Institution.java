package org.usth.ict.ulake.user.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
public class Institution  extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;
    public String name;

    // @JsonManagedReference
    @OneToMany(mappedBy = "institution")
    public List<Department> departments;

    public Institution() { }
}
