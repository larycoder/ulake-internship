package org.usth.ict.query.model.query;

public class FilterModel {
    private String property;
    private OpModel op;
    private String value;

    public FilterModel(String property, OpModel op, String value) {
        this.property = property;
        this.op = op;
        this.value = value;
    }

    /**
     * Parsing form string and updating corresponding parameters
     * */
    public FilterModel(String param) {
        String[] tokens = param.split("\\s+", 3);
        property = tokens[0];
        op = new OpModel(tokens[1]);
        value = tokens[2];
    }

    public Boolean filter(Queryable dataObj) {
        return dataObj.filter(property, value, op);
    }

    public String getProperty() {
        return property;
    }
    public void setProperty(String property) {
        this.property = property;
    }
    public OpModel getOp() {
        return op;
    }
    public void setOp(OpModel op) {
        this.op = op;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
