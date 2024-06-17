package org.usth.ict.ulake.user.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Department {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;
    public String name;
    public String address;

    //@JsonBackReference("departments")
    @ManyToOne
    @JoinColumn
    public Institution institution;

    //@JsonManagedReference("department")
    @OneToMany(mappedBy = "department")
    public List<User> users;

    public Department() { }
}
