package org.usth.ict.ulake.compress.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.compress.model.CompressRequest;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class RequestRepository implements PanacheRepository<CompressRequest> {
}
