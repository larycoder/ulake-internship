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
import org.usth.ict.ulake.table.model.TableMetadata;
import org.usth.ict.ulake.table.model.TableModel;
import org.usth.ict.ulake.table.persistence.TableCellRepository;
import org.usth.ict.ulake.table.persistence.TableColumnRepository;
import org.usth.ict.ulake.table.persistence.TableRepository;
import org.usth.ict.ulake.table.persistence.TableRowRepository;

public class Xlsx implements Parser {
    Logger log = LoggerFactory.getLogger(Csv.class);

    @Override
    public Table parse(TableRepository repo, TableRowRepository repoRow, TableColumnRepository repoColumn,
            TableCellRepository repoCell, InputStream is, TableModel tableModel, TableMetadata metadata) {
        Table table = new Table();
        table.model = tableModel;
        table.columns = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);

            Map<Integer, List<String>> data = new HashMap<>();
            int i = 0;
            for (Row row : sheet) {
                data.put(i, new ArrayList<String>());
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
                        default: data.get(new Integer(i)).add(" ");
                    }
                    log.info("col {}, value: {}", cell.getColumnIndex(), value);
                }
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return table;
    }

}
