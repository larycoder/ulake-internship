package org.usth.ict.ulake.user.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;

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
