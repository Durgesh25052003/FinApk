package com.fintech.transactionControl.Exception;

public class ForbiddenAccess extends RuntimeException {
    public ForbiddenAccess(String message) {
        super(message);
    }
}
