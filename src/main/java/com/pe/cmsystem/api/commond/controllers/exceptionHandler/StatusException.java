package com.pe.cmsystem.api.commond.controllers.exceptionHandler;

import org.springframework.http.HttpStatus;

public class StatusException extends RuntimeException {
    private final HttpStatus statusCode;

    public StatusException(String message, HttpStatus statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public StatusException(String message, HttpStatus statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }
}
