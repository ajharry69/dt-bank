package com.github.ajharry69.customer.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DTBException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String errorCode;

    public DTBException(HttpStatus httpStatus, String errorCode) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
}

