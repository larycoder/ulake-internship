package org.usth.ict.ulake.core.backend;

import java.util.ArrayList;
import java.util.Map;

public interface Dbms {
    String createTable(String name, ArrayList<Map.Entry<String, String>> columnTypes);
    ArrayList<Map.Entry<String, String>> getTableSchema(String name);
    int insertRow(String tableName, ArrayList<Map.Entry<String, String>> columnValues);
    boolean deleteRow(String tableName, int id);
}
