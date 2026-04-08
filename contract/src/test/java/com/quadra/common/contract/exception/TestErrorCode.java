package com.quadra.common.contract.exception;

import com.quadra.common.core.exception.ErrorCode;

public enum TestErrorCode implements ErrorCode {

    TEST_ERROR1(401, "TST001", "It is T001"),
    TEST_ERROR2(402, "TST002", "It is T002");

    private final int status;
    private final String code;
    private final String message;

    TestErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
