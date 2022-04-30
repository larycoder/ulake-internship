package org.usth.ict.query.model.query;

public class QueryException extends Exception {
    public QueryException(String msg) {
        super("Query Exception with message: " + msg);
    }
}
