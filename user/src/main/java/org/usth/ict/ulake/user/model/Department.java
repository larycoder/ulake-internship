package org.usth.ict.ulake.user.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
public class Department {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;
    public String name;
    public String address;

    @JsonBackReference
    @ManyToOne
    @JoinColumn
    public Institution institution;

    public Department() { }
}
