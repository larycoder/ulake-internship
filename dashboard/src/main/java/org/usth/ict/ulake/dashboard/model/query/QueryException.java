package org.usth.ict.ulake.dashboard.model.query;

public class QueryException extends Exception {
    public QueryException(String msg) {
        super("Query Exception with message: " + msg);
    }
}
