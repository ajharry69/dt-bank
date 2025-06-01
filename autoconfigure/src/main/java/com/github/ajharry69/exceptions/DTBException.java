package com.github.ajharry69.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class DTBException extends ResponseStatusException {
    private final HttpStatus httpStatus;
    private final String errorCode;

    public DTBException(HttpStatus httpStatus, String errorCode) {
        super(HttpStatusCode.valueOf(httpStatus.value()));
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

