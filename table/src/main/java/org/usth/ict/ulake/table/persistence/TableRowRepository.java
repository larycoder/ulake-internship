package org.usth.ict.ulake.table.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.table.model.TableRowModel;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class TableRowRepository implements PanacheRepository<TableRowModel> {
}
