package org.usth.ict.ulake.user.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

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
