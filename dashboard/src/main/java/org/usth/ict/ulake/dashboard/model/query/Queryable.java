package org.usth.ict.ulake.dashboard.model.query;

public interface Queryable {
    public Boolean filter(String property, String value, OpModel op);
}
