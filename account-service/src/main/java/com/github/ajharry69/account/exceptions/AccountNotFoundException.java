package com.github.ajharry69.account.exceptions;

import com.github.ajharry69.exceptions.DTBException;
import org.springframework.http.HttpStatus;

public class AccountNotFoundException extends DTBException {
    public AccountNotFoundException() {
        super(HttpStatus.NOT_FOUND, "ACCOUNT_NOT_FOUND");
    }
}
