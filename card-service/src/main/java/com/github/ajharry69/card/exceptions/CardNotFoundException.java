package com.github.ajharry69.card.exceptions;

import com.github.ajharry69.exceptions.DTBException;
import org.springframework.http.HttpStatus;

public class CardNotFoundException extends DTBException {
    public CardNotFoundException() {
        super(HttpStatus.NOT_FOUND, "CARD_NOT_FOUND");
    }
}
