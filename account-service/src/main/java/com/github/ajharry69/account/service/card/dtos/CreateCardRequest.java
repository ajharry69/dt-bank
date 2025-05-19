package com.github.ajharry69.account.service.card.dtos;

import com.github.ajharry69.account.models.CardType;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCardRequest {
    String alias;
    String pan;
    String cvv;
    CardType type;
    UUID accountId;
}
