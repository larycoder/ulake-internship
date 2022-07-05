package org.usth.ict.ulake.extract.persistence;

import javax.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.extract.model.ExtractResult;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class ExtractResultRepository implements PanacheRepository<ExtractResult> {
}
