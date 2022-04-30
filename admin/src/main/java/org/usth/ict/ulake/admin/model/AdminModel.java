package org.usth.ict.ulake.admin.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AdminModel {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;
}
