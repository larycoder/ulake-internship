package org.usth.ict.ulake.user.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Allow management of users in groups
 */
@Entity
public class UserGroup extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;

    public String name;

    @JsonBackReference
    @ManyToMany
    @JoinColumn
    public Set<User> users = new HashSet<>();
}
