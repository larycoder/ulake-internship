package org.usth.ict.ulake.ingest.crawler.fetcher.cpl.struct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TableStruct<T> {
    List<String> key;
    List<List<T>> table;

    private void error(String message) {
        throw new TableStructException(message);
    }

    public TableStruct(List<String> key, List<List<T>> data) {
        if (new HashSet<>(this.key).size() != this.key.size())
            error("Duplicate fields.");

        this.key = new ArrayList<>(key);
        this.table = data;
    }

    public TableStruct(List<String> key) {
        this(key, new ArrayList<>());
    }

    public TableStruct() {
        this.key = new ArrayList<>();
        this.table = new ArrayList<>();
    }

    public void addKey(String field) {
        if (key.contains(field))
            error("Field existed.");

        key.add(field);
        for (var row : table)
            row.add(null);
    }

    public void setKey(List<String> key) {
        drop();
        this.key.addAll(key);
    }

    public void add(Map<String, T> data) {
        List<T> row = new ArrayList<>();
        for (String field : this.key)
            row.add(data.get(field));
        table.add(row);
    }

    public void add(List<T> data) {
        if (key.size() != data.size())
            error("Data size mismatch with key size.");
        table.add(data);
    }

    public void add(String key, T data) {
        Map<String, T> dataMap = new HashMap<>();
        dataMap.put(key, data);
        add(dataMap);
    }

    public int rowSize() {
        return table.size();
    }

    public int colSize() {
        return key.size();
    }

    public List<T> getCol(String field) {
        int idx = key.indexOf(field);
        List<T> col = new ArrayList<>();
        for (var row : table) {
            col.add(row.get(idx));
        }
        return col;
    }

    public List<T> getRow(int idx) {
        List<T> row = new ArrayList<>();
        row.addAll(table.get(idx));
        return row;
    }

    public List<String> getKey() {
        List<String> rstKey = new ArrayList<>();
        rstKey.addAll(key);
        return rstKey;
    }

    private Map<String, T> wrapDataToMap(List<T> data) {
        Map<String, T> wrapper = new HashMap<>();
        for (int i = 0; i < key.size(); i++) {
            wrapper.put(key.get(i), data.get(i));
        }
        return wrapper;
    }

    public List<T> stackPop() {
        return table.remove(table.size() - 1);
    }

    public List<T> queuePop() {
        return table.remove(0);
    }

    public Map<String, T> stackPopJson() {
        return wrapDataToMap(stackPop());
    }

    public Map<String, T> queuePopJson() {
        return wrapDataToMap(queuePop());
    }

    public void clear() {
        /*clear data of table*/
        table.clear();
    }

    public void drop() {
        /*clear data and key of table*/
        clear();
        key.clear();
    }

    public TableStruct<T> clone() {
        TableStruct<T> newTable = new TableStruct<>(new ArrayList<>(key));
        for (var row : table)
            newTable.add(new ArrayList<>(row));
        return newTable;
    }

    public List<List<Object>> extractAsList() {
        var tableList = new ArrayList<List<Object>>();
        tableList.add(new ArrayList<Object>(key));
        for (List<T> row : table)
            tableList.add(new ArrayList<Object>(row));
        return tableList;
    }

    /**
     * Iterating throw each row and return it as map structure.
     * */
    public Iterable<Map<String, T>> mapRowList() {
        return () -> new Iterator<Map<String, T>>() {
            int pos = 0;

            @Override
            public boolean hasNext() {
                return ( pos < TableStruct.this.rowSize() );
            }

            @Override
            public Map<String, T> next() {
                List<T> data = table.get(pos);
                Map<String, T> dataMap = TableStruct.this.wrapDataToMap(data);
                pos += 1;
                return dataMap;
            }
        };
    }

    public String toString() {
        Map<String, Object> present = new HashMap<>();
        present.put("key", key);
        present.put("data", table);
        return present.toString();
    }
}
