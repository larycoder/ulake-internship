package org.usth.ict.ulake.table.persistence;

import javax.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.table.model.TableCellModel;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class TableCellRepository implements PanacheRepository<TableCellModel> {
}
