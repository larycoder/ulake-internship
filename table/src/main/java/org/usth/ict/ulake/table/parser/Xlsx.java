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
import org.usth.ict.ulake.table.model.Table;
import org.usth.ict.ulake.table.model.TableMetadata;
import org.usth.ict.ulake.table.model.TableModel;
import org.usth.ict.ulake.table.persistence.TableCellRepository;
import org.usth.ict.ulake.table.persistence.TableColumnRepository;
import org.usth.ict.ulake.table.persistence.TableRepository;
import org.usth.ict.ulake.table.persistence.TableRowRepository;

public class Xlsx implements Parser {

    @Override
    public Table parse(TableRepository repo, TableRowRepository repoRow, TableColumnRepository repoColumn,
            TableCellRepository repoCell, InputStream is, TableModel tableModel, TableMetadata metadata) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);

            Map<Integer, List<String>> data = new HashMap<>();
            int i = 0;
            for (Row row : sheet) {
                data.put(i, new ArrayList<String>());
                for (Cell cell : row) {

                    // switch (cell.getCellType()) {
                    //     case STRING: ... break;
                    //     case NUMERIC: ... break;
                    //     case BOOLEAN: ... break;
                    //     case FORMULA: ... break;
                    //     default: data.get(new Integer(i)).add(" ");
                    // }
                }
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
