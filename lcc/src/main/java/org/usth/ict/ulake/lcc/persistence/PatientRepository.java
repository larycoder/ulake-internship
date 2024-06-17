package org.usth.ict.ulake.lcc.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.lcc.model.Patient;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class PatientRepository implements PanacheRepository<Patient> {
}
