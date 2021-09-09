package org.usth.ict.ulake.user.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
public class User extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;
    public String userName;
    public String firstName;
    public String lastName;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String email;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
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

    @JsonIgnore
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
