package org.usth.ict.ulake.ingest.persistence;

import javax.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.ingest.model.ProcessLog;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class ProcessLogRepo implements PanacheRepository<ProcessLog> {
}
