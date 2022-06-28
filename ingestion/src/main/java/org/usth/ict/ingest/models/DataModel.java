package org.usth.ict.ingest.models;

import java.util.ArrayList;
import java.util.List;

public class DataModel {
    private int count = 0;
    private List data = null;
    private List head = null;

    public DataModel(List list) {
        setList(list);
    }

    public DataModel(List head, List data) {
        this.head = head;
        setList(data);
    }

    public DataModel() {
        List list = new ArrayList();
        setList(list);
    }

    public DataModel setList(List list) {
        data = list;
        count = list.size();
        return this;
    }

    public DataModel insert(Object record) {
        data.add(record);
        return this;
    }

    public List getHead() {
        return head;
    }

    public List getData() {
        return data;
    }

    public int getCount() {
        return count;
    }

}
