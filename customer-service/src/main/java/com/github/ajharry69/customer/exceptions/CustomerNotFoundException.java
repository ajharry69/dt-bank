package com.github.ajharry69.customer.exceptions;

import org.springframework.http.HttpStatus;

public class CustomerNotFoundException extends DTBException {
    public CustomerNotFoundException() {
        super(HttpStatus.NOT_FOUND, "CUSTOMER_NOT_FOUND");
    }
}
