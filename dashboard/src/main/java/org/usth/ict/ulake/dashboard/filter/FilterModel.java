package org.usth.ict.ulake.dashboard.filter;

import org.usth.ict.ulake.dashboard.filter.impl.OperatorFactory;

public class FilterModel {
    public String property;
    public Operator<?, ?, ?> op;
    public String value;

    public FilterModel(String property, Operator<?, ?, ?> op, String value) {
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
        op = OperatorFactory.getOp(tokens[1]);
        value = tokens[2];
    }
}
