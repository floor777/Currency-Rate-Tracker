package com.currencyratetracker.crt.exceptions;

import org.springframework.http.HttpStatus;

public class AppException extends RuntimeException {
    public final HttpStatus httpStatus;
    public AppException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;

    }
    
}
