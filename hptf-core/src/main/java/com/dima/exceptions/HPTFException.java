package com.dima.exceptions;

public class HPTFException extends Exception {
    public HPTFException(String cause) {
        super(cause);
    }

    public HPTFException(String cause, Exception ex) {
        super(cause, ex);
    }
}
