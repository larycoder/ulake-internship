package org.usth.ict.ulake.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;

@Entity
public class User extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;
    public String userName;
    public String firstName;
    public String lastName;
    public String email;
    public String password;
    public Long registerTime;

    @JsonBackReference
    @ManyToOne
    @JoinColumn
    public Department department;

    @JsonBackReference
    @ManyToOne
    @JoinColumn
    public UserGroup group;

}
