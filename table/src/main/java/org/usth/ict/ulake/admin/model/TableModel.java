package org.usth.ict.ulake.admin.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TableModel {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;
}
