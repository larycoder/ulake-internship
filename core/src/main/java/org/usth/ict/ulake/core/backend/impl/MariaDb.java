package org.usth.ict.ulake.core.backend.impl;

import org.usth.ict.ulake.core.backend.Dbms;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MariaDb implements Dbms {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public String createTable(String name, ArrayList<Map.Entry<String, String>> columnTypes) {
        return null;
    }

    @Override
    public ArrayList<Map.Entry<String, String>> getTableSchema(String name) {
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
