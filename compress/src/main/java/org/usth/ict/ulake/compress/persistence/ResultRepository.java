package org.usth.ict.ulake.compress.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.compress.model.CompressResult;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class ResultRepository implements PanacheRepository<CompressResult> {
}
