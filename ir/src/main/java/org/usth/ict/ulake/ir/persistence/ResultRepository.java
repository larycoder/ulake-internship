package org.usth.ict.ulake.ir.persistence;

import javax.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.ir.model.Result;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class ResultRepository implements PanacheRepository<Result> {
}
