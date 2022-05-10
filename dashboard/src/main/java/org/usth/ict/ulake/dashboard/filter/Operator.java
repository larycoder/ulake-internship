package org.usth.ict.ulake.dashboard.filter;

/**
 * Operator interface to filter data
 * @param <D> single data object type
 * @param <P> property type
 * @param <V> comparison value type
 * */
public interface Operator<D, P, V> {
    /**
     * Interface to verify data passing filter
     * */
    public Boolean verify(D data, P property, V value) throws QueryException;
}
