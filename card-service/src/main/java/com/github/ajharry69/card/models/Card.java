package com.github.ajharry69.card.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity(name = "cards")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String alias;
    @Column(nullable = false)
    private String pan;
    @Column(nullable = false)
    private String cvv;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CardType type;
    @Column(nullable = false)
    private UUID accountId;
}
