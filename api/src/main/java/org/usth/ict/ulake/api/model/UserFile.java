package org.usth.ict.ulake.api.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * A file is mapped to an object in lake storage
 */
public class UserFile extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;
    public String cid;
    public String name;


}
