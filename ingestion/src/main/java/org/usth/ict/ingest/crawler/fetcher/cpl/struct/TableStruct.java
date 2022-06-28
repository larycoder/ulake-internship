package org.usth.ict.ingest.crawler.fetcher.cpl.struct;

import java.util.*;
import java.util.function.Consumer;

public class TableStruct {
    List<String> key;
    List<List<Object>> table;

    public TableStruct(List key, List data) {
        this.key = new ArrayList<>(key);
        this.table = data;

        Set checkSet = new HashSet(this.key);
        if(checkSet.size() != this.key.size()) error("Duplicate fields.");
    }

    public TableStruct(List key) {
        this(key, new ArrayList<>());
    }

    public TableStruct() {
        this.key = new ArrayList<>();
        this.table = new ArrayList<>();
    }

    private void error(String message) {
        throw new RuntimeException(message);
    }

    public void add(Map<String, Object> data) {
        List row = new ArrayList();
        for(String field : this.key) {
            row.add(data.get(field));
        }
        table.add(row);
    }

    public void add(List data) {
        if(key.size() != data.size()) {
            error("Data size mismatch with key size.");
        }
        table.add(data);
    }

    public void add(String key, Object data) {
        Map dataMap = new HashMap();
        dataMap.put(key, data);
        add(dataMap);
    }

    public void addKey(String field) {
        if(key.contains(field)) {
            error("Field existed.");
        }
        key.add(field);
        for(var row : table) {
            row.add(null);
        }
    }

    public void setKey(List key) {
        drop();
        this.key.addAll(key);
    }

    public int rowSize() {
        return table.size();
    }

    public int colSize() {
        return key.size();
    }

    public List getCol(String field) {
        int idx = key.indexOf(field);
        List col = new ArrayList();
        for(var row : table) {
            col.add(row.get(idx));
        }
        return col;
    }

    public List getRow(int idx) {
        List row = new ArrayList();
        row.addAll(table.get(idx));
        return row;
    }

    private List getList(List list) {
        List result = new ArrayList<>();
        for(var field : list) {
            result.add(field);
        }
        return result;
    }

    public List<String> getKey() {
        return new ArrayList<String>(getList(key));
    }

    private Map convertToJson(List data) {
        Map obj = new HashMap();
        for(int i=0; i < key.size(); i++) {
            obj.put(key.get(i), data.get(i));
        }
        return obj;
    }

    public List stackPop() {
        return table.remove(table.size() - 1);
    }

    public Map stackPopJson() {
        return convertToJson(stackPop());
    }

    public List queuePop() {
        return table.remove(0);
    }

    public Map queuePopJson() {
        return convertToJson(queuePop());
    }

    public TableStruct clone() {
        List<String> key = new ArrayList(this.key);
        TableStruct instance = new TableStruct(key);

        for(var row : table) {
            List cloneRow = new ArrayList();
            cloneRow.addAll(row);
            instance.add(cloneRow);
        }

        return instance;
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

    private List getFullData() {
        return table;
    }

    public List<List> extractAsList() {
        TableStruct instance = clone();
        List table = instance.getFullData();
        table.add(0, instance.getKey());
        return table;
    }

    public String toString() {
        Map present = new HashMap();
        present.put("key", key);
        present.put("data", table);
        return present.toString();
    }

    public Iterable<Map> rowList() {
        return () -> new Iterator<Map>() {
            int pos = 0;

            @Override
            public boolean hasNext() {
                if(pos < TableStruct.this.rowSize()) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public Map next() {
                List data = TableStruct.this.getRow(pos);
                Map json = TableStruct.this.convertToJson(data);

                Map result = new HashMap();
                result.put("list", data);
                result.put("json", json);

                pos += 1;
                return result;
            }
        };
    }
}
