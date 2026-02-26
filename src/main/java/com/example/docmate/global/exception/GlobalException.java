package com.example.docmate.global.exception;

import org.springframework.http.HttpStatus;

public class GlobalException extends RuntimeException{

    private HttpStatus httpStatus;
    public GlobalException(String message, HttpStatus status) {
        super(message);
        this.httpStatus = status;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
