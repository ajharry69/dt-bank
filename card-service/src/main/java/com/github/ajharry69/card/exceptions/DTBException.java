package com.github.ajharry69.card.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class DTBException extends ResponseStatusException {
    private final HttpStatus httpStatus;
    private final String errorCode;

    public DTBException(HttpStatus httpStatus, String errorCode) {
        super(HttpStatusCode.valueOf(httpStatus.value()));
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
}

