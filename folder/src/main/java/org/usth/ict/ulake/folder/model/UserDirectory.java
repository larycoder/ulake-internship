package org.usth.ict.ulake.folder.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * A directory is mapped to a group in lake
 * Root directory can be mapped to a dataset
 */
@Entity
public class UserDirectory extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;
    public Long coreGroupId;
    public String name;
}
