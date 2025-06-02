package com.github.ajharry69.exceptions;

import org.springframework.http.HttpStatus;

public class DTBAccessDeniedException extends DTBException {
    public DTBAccessDeniedException() {
        super(HttpStatus.FORBIDDEN, "ACCESS_DENIED");
    }
}
