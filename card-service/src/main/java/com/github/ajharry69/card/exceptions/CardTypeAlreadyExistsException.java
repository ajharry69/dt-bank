package com.github.ajharry69.card.exceptions;

import com.github.ajharry69.exceptions.DTBException;
import org.springframework.http.HttpStatus;

public class CardTypeAlreadyExistsException extends DTBException {
    public CardTypeAlreadyExistsException() {
        super(HttpStatus.ALREADY_REPORTED, "CARD_TYPE_ALREADY_EXISTS");
    }
}
