package org.usth.ict.ulake.user.persistence;

import javax.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.user.model.Institution;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class InstitutionRepository implements PanacheRepository<Institution> {
}
