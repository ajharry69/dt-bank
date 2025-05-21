package com.github.ajharry69.account.service.card.models.dtos;

import com.github.ajharry69.account.service.card.models.CardType;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountCardRequest {
    String alias;
    String pan;
    String cvv;
    CardType type;
    UUID accountId;
}
