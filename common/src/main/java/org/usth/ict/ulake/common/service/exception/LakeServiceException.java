package org.usth.ict.ulake.common.service.exception;

public class LakeServiceException extends Exception {
    private static final long serialVersionUID = 1L;
    public LakeServiceException(String msg) {
        super(msg);
    }

    public LakeServiceException() {
        super();
    }
}
