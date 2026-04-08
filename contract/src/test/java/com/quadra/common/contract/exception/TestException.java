package com.quadra.common.contract.exception;

import com.quadra.common.core.exception.BaseException;
import com.quadra.common.core.exception.ErrorCode;

public class TestException extends BaseException {

    public TestException(ErrorCode errorCode) {
        super(errorCode);
    }

    public TestException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
