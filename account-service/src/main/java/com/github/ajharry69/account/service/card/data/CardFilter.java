package com.github.ajharry69.account.service.card.data;

import com.github.ajharry69.account.service.card.models.CardType;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CardFilter {
    private Boolean unmask;
    private UUID accountId;
    private String pan;
    private String alias;
    private CardType type;
    private LocalDate startDateCreated;
    private LocalDate endDateCreated;
}