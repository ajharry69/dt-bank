package com.github.ajharry69.customer.exceptions;

import org.springframework.http.HttpStatus;

public class DuplicateCustomerException extends DTBException {
    public DuplicateCustomerException() {
        super(HttpStatus.BAD_REQUEST, "DUPLICATE_CUSTOMER");
    }
}
