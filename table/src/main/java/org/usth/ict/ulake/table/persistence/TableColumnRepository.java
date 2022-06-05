package org.usth.ict.ulake.table.persistence;

import javax.enterprise.context.ApplicationScoped;

import org.usth.ict.ulake.table.model.TableColumnModel;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class TableColumnRepository implements PanacheRepository<TableColumnModel> {
}
