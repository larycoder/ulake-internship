package org.usth.ict.ulake.user.model;

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

    @ManyToMany(cascade = {
        CascadeType.PERSIST,
        CascadeType.MERGE
    })
    @JoinTable(name = "UserGroup_User",
        joinColumns = @JoinColumn(name = "UserGroup_id"),
        inverseJoinColumns = @JoinColumn(name = "users_id")
    )
    public Set<User> users = new HashSet<>();
}
