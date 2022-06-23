package org.usth.ict.ulake.table.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usth.ict.ulake.table.model.Table;
import org.usth.ict.ulake.table.model.TableCellModel;
import org.usth.ict.ulake.table.model.TableColumnModel;
import org.usth.ict.ulake.table.model.TableMetadata;
import org.usth.ict.ulake.table.model.TableModel;
import org.usth.ict.ulake.table.model.TableRowModel;
import org.usth.ict.ulake.table.persistence.TableCellRepository;
import org.usth.ict.ulake.table.persistence.TableColumnRepository;
import org.usth.ict.ulake.table.persistence.TableRepository;
import org.usth.ict.ulake.table.persistence.TableRowRepository;

public class Csv implements Parser {
    Logger log = LoggerFactory.getLogger(Csv.class);

    @Override
    public Table parse(TableRepository repo, TableRowRepository repoRow,
                TableColumnRepository repoColumn, TableCellRepository repoCell,
                InputStream is, TableModel tableModel, TableMetadata metadata) {
        Table table = new Table();
        table.model = tableModel;
        table.columns = new ArrayList<>();
        Reader ir = new InputStreamReader(is);
        try {
            Iterable<CSVRecord> records;
            records = CSVFormat.DEFAULT.parse(ir);
            var it = records.iterator();
            int rowIndex = 0;
            int cells = 0;
            while (it.hasNext()) {
                var rowCsv = it.next();
                TableRowModel row = new TableRowModel();
                row.table = table.model;
                repoRow.persist(row);

                if (table.columns.size() == 0) {
                    // first row should be header
                    for (var key: rowCsv) {
                        TableColumnModel column = new TableColumnModel();
                        column.columnName = key.toString().trim().replaceAll("^\"|\"$", "");
                        column.dataType = "string";     // default...
                        column.table = table.model;
                        repoColumn.persist(column);
                        table.columns.add(column);
                    }
                }
                else {
                    int index = 0;
                    for (var key: rowCsv) {
                        String value = key.toString().trim().replaceAll("^\"|\"$", "");
                        if (index < table.columns.size()) {
                            TableCellModel cell = new TableCellModel();
                            cell.column = table.columns.get(index++);
                            cell.row = row;
                            cell.table = tableModel;
                            cell.value = value;
                            repoCell.persist(cell);
                            cells++;
                        }
                        else {
                            log.warn("Row {}: out of column {}, value {}", rowIndex, index, value);
                        }
                    }
                }
                rowIndex++;
            }
            log.info("Imported {} rows, {} columns and {} cells", rowIndex, table.columns.size(), cells);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return table;
    }
}
