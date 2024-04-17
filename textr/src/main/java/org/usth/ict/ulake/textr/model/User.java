package org.usth.ict.ulake.textr.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;

@Entity
public class User extends PanacheEntityBase {
    @Column(length = 100)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(length = 200)
    private String Name;

//    Setter
    public void setName(String name) {
        Name = name;
    }

//    Getter
    public Long getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }
}
