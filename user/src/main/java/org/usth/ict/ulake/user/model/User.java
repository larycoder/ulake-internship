package org.usth.ict.ulake.user.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public String refreshToken;

    @JsonBackReference
    @ManyToOne
    @JoinColumn
    public Department department;

    @JsonBackReference
    @ManyToMany
    @JoinColumn
    public Set<UserGroup> groups = new HashSet<>();
}
