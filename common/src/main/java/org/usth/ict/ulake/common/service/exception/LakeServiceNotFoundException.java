package org.usth.ict.ulake.common.service.exception;

public class LakeServiceNotFoundException extends LakeServiceException {
    private static final long serialVersionUID = 1L;
    public LakeServiceNotFoundException(String msg) {
        super(msg);
    }
}
