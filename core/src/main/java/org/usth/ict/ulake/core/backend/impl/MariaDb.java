package org.usth.ict.ulake.core.backend.impl;

import org.usth.ict.ulake.core.backend.Dbms;

import java.util.ArrayList;
import java.util.Map;

public class MariaDb implements Dbms {
    @Override
    public String createTable(String name, ArrayList<Map.Entry<String, String>> columnTypes) {
        return null;
    }

    @Override
    public ArrayList<Map<String, String>> getTableSchema(String name) {
        return null;
    }

    @Override
    public int insertRow(String tableName, ArrayList<Map.Entry<String, String>> columnValues) {
        return 0;
    }

    @Override
    public boolean deleteRow(String tableName, int id) {
        return false;
    }
}
