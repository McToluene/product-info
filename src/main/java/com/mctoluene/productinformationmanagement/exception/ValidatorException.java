package com.mctoluene.productinformationmanagement.exception;

public class ValidatorException extends RuntimeException {

    public ValidatorException(String message, Exception cause) {
        super(message, cause);
    }

    public ValidatorException(String message) {
        super(message);
    }
}
