package org.usth.ict.ulake.table.persistence;

import javax.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.table.model.TableModel;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class TableRepository implements PanacheRepository<TableModel> {
}
