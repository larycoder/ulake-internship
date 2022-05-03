package org.usth.ict.ulake.common.service;

import java.io.ByteArrayInputStream;

import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import org.usth.ict.ulake.common.service.exception.LakeServiceException;
import org.usth.ict.ulake.common.service.exception.LakeServiceNotFoundException;

public class LakeServiceExceptionMapper
    implements ResponseExceptionMapper<LakeServiceException> {

    @Override
    public LakeServiceException toThrowable(Response response) {
        switch (response.getStatus()) {
        case 404:
            return new LakeServiceNotFoundException("Not found error");
        }
        return null;
    }

}
