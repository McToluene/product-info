package com.mctoluene.productinformationmanagement.exception;

public class UnProcessableEntityException extends RuntimeException {

    public UnProcessableEntityException(String message) {
        super(message);
    }

    public UnProcessableEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}
