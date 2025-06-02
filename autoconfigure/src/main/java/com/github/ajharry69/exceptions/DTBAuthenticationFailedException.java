package com.github.ajharry69.exceptions;

import org.springframework.http.HttpStatus;

public class DTBAuthenticationFailedException extends DTBException {
    public DTBAuthenticationFailedException() {
        super(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_FAILED");
    }
}
