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

        Iterable<CSVRecord> records;
        try {
            records = CSVFormat.DEFAULT.parse(ir);
            for (CSVRecord record : records) {
                var map = record.toMap();
                for (var key: map.keySet()) {
                    System.out.println("CSV " + key + " = " + map.get(key));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return table;
    }
}
