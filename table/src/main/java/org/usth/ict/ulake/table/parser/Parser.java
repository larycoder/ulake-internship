package org.usth.ict.ulake.table.parser;

import java.io.InputStream;

import org.slf4j.Logger;
import org.usth.ict.ulake.table.model.Table;
import org.usth.ict.ulake.table.model.TableMetadata;
import org.usth.ict.ulake.table.model.TableModel;
import org.usth.ict.ulake.table.persistence.TableCellRepository;
import org.usth.ict.ulake.table.persistence.TableColumnRepository;
import org.usth.ict.ulake.table.persistence.TableRepository;
import org.usth.ict.ulake.table.persistence.TableRowRepository;

public interface Parser {
    public Table parse(TableRepository repo, TableRowRepository repoRow,
                TableColumnRepository repoColumn, TableCellRepository repoCell,
                InputStream is, TableModel tableModel, TableMetadata metadata);
}
