package com.mctoluene.productinformationmanagement.exception;

public class StockOneException extends RuntimeException {

    public StockOneException(String message) {
        super(message);
    }

    public StockOneException(String message, Throwable cause) {
        super(message, cause);
    }
}
