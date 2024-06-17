package org.usth.ict.ulake.compress.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.compress.model.CompressRequestFile;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class RequestFileRepository implements PanacheRepository<CompressRequestFile> {
}
