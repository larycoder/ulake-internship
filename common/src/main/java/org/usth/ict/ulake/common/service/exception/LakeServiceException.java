package org.usth.ict.ulake.common.service.exception;

public class LakeServiceException extends RuntimeException {
    public LakeServiceException(String msg) {
        super(msg);
    }

    public LakeServiceException() {
        super();
    }
}
