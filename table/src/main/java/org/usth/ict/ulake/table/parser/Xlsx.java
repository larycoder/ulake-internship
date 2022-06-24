package org.usth.ict.ulake.table.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

public class Xlsx implements Parser {
    Logger log = LoggerFactory.getLogger(Csv.class);

    private Map<Integer, String> parseRow(Row row) {
        Map<Integer, String> cellValues = new HashMap<>();
        for (Cell cell : row) {
            String value = "";
            switch (cell.getCellType()) {
                case STRING: value = cell.getStringCellValue(); break;
                case NUMERIC: value = String.valueOf(cell.getNumericCellValue()); break;
                case BOOLEAN: value = String.valueOf(cell.getBooleanCellValue()); break;
                case FORMULA:
                    switch (cell.getCachedFormulaResultType()) {
                        case BOOLEAN: value = String.valueOf(cell.getBooleanCellValue()); break;
                        case NUMERIC: value = String.valueOf(cell.getNumericCellValue()); break;
                        case STRING: value = cell.getStringCellValue(); break;
                    }
                default: value = "";
            }
            cellValues.put(cell.getColumnIndex(), value);
        }
        return cellValues;
    }

    @Override
    public Table parse(TableRepository repo, TableRowRepository repoRow, TableColumnRepository repoColumn,
            TableCellRepository repoCell, InputStream is, TableModel tableModel, TableMetadata metadata) {
        Table table = new Table();
        table.model = tableModel;
        table.columns = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            int rowIndex = 0;
            for (Row row : sheet) {
                TableRowModel rowModel = new TableRowModel();
                rowModel.table = table.model;
                repoRow.persist(rowModel);

                var cellValues = parseRow(row);
                if (table.columns.size() == 0) {
                    // first row should be header
                    for (var key: cellValues.values()) {
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
                    for (var key: cellValues.values()) {
                        String value = key.toString().trim().replaceAll("^\"|\"$", "");
                        if (index < table.columns.size()) {
                            TableCellModel cell = new TableCellModel();
                            cell.column = table.columns.get(index++);
                            cell.row = rowModel;
                            cell.table = tableModel;
                            cell.value = value;
                            repoCell.persist(cell);
                        }
                        else {
                            log.warn("Row {}: out of column {}, value {}", rowIndex, index, value);
                        }
                    }
                }

            }
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return table;
    }

}
