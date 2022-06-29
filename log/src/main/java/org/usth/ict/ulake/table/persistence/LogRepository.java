package org.usth.ict.ulake.table.persistence;

import javax.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.table.model.LogEntry;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class LogRepository implements PanacheRepository<LogEntry> {
}
