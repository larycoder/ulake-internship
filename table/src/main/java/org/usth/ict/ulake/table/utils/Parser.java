package org.usth.ict.ulake.table.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.usth.ict.ulake.table.model.Table;
import org.usth.ict.ulake.table.model.TableMetadata;
import org.usth.ict.ulake.table.model.TableModel;

public class Parser {
    public static Table parseCsv(InputStream is, TableMetadata metadata) {
        Table table = new Table();
        table.model = new TableModel();
        table.model.name = metadata.name;
        table.model.format = metadata.format;

        Reader ir = new InputStreamReader(is);        
        try {
            Iterable<CSVRecord> records;
            records = CSVFormat.DEFAULT.parse(ir);
            var it = records.iterator();
            while (it.hasNext()) {                
                var row = it.next();
                System.out.println("Row size : " + row.size());                
                for (var key: row) {
                    System.out.println("CSV " + key);
                }                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return table;
    }
}
