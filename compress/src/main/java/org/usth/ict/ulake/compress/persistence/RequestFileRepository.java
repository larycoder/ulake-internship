package org.usth.ict.ulake.compress.persistence;

import javax.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.compress.model.RequestFile;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class RequestFileRepository implements PanacheRepository<RequestFile> {
}
