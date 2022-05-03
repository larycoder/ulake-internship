package org.usth.ict.ulake.search.service;

import org.usth.ict.ulake.common.model.LakeHttpResponse;

/**
 * Generic interface for all service search
 * (service much support this interface to integrate to search service)
 * @param <Q> service query model
 */
public interface SearchService<Q> {
    public LakeHttpResponse search(String bearer, Q query);
}
