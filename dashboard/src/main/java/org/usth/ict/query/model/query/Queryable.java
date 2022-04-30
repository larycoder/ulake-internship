package org.usth.ict.query.model.query;

public interface Queryable {
    public Boolean filter(String property, String value, OpModel op);
}
