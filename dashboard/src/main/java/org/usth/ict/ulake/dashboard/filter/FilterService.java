package org.usth.ict.ulake.dashboard.filter;

/**
 * Filter interface for retrieving data throw user filter
 * @param <D> system input data type
 * @param <F> user filter type
 * @param <R> response data type
 * */
public interface FilterService<D, F, R> {
    public R filter(D data, F filter) throws QueryException;
}
