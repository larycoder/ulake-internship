package org.usth.ict.ulake.dashboard.filter;

public class QueryException extends Exception {
    public QueryException(String msg) {
        super("Query Exception with message: " + msg);
    }
}
