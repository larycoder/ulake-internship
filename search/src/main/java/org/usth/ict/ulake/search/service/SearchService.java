package org.usth.ict.ulake.search.service;

import org.usth.ict.ulake.common.model.LakeHttpResponse;
import org.usth.ict.ulake.common.service.exception.LakeServiceException;

/**
 * Generic interface for all service search
 * (service much support this interface to integrate to search service)
 * @param <Q> service query model
 */
public interface SearchService<Q> {
    public LakeHttpResponse search(String bearer, Q query)
    throws LakeServiceException;
}
