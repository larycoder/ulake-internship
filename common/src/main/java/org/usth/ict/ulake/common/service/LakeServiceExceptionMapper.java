package org.usth.ict.ulake.common.service;

import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import org.usth.ict.ulake.common.service.exception.LakeServiceException;
import org.usth.ict.ulake.common.service.exception.LakeServiceForbiddenException;
import org.usth.ict.ulake.common.service.exception.LakeServiceInternalException;
import org.usth.ict.ulake.common.service.exception.LakeServiceNotFoundException;

public class LakeServiceExceptionMapper
    implements ResponseExceptionMapper<LakeServiceException> {

    @Override
    public LakeServiceException toThrowable(Response response) {
        switch (response.getStatus()) {
            case 500:
                return new LakeServiceInternalException("Internal server error");
            case 404:
                return new LakeServiceNotFoundException("Not found error");
            case 403:
                return new LakeServiceForbiddenException("Forbidden error");
            default:
                return new LakeServiceException("Unknown error");
        }
    }

}
