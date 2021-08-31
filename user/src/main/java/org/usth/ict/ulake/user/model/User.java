package org.usth.ict.ulake.user.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class User extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;
    public String userName;
    public String firstName;
    public String lastName;
    @JsonIgnore
    public String email;
    @JsonIgnore
    public String password;
    public Long registerTime;

    @Column(columnDefinition = "varchar(5000)")
    @JsonIgnore
    public String accessToken;
    @JsonIgnore
    public String refreshToken;
    @JsonIgnore
    public Long refreshTokenExpire;

    // @JsonBackReference("department")
    @ManyToOne
    @JoinColumn
    public Department department;

    //@JsonBackReference("group")
    @ManyToMany
    @JoinColumn
    public Set<UserGroup> groups = new HashSet<>();
}
