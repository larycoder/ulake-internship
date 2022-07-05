package org.usth.ict.ulake.extract.persistence;

import javax.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.extract.model.ExtractRequest;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class ExtractRequestRepository implements PanacheRepository<ExtractRequest> {
}
