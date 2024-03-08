package com.mctoluene.productinformationmanagement.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PageableException extends RuntimeException {
    public PageableException(String message, Exception cause) {
        super(message, cause);
    }

    public PageableException(String message) {
        super(message);
    }
}