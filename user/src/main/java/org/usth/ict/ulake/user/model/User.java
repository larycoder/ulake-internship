package org.usth.ict.ulake.user.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
public class User extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public String userName;
    public String firstName;
    public String lastName;
    public Long failedLogins;
    public Boolean isAdmin;

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

    @Schema(description = "Status of activate account")
    public Boolean status;

    @JsonIgnore
    @Schema(description = "Activate code sent by mail")
    public String code;

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
