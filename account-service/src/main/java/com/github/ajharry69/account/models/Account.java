package com.github.ajharry69.account.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity(name = "accounts")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String iban;
    @Column(nullable = false)
    private String bicSwift;
    @Column(nullable = false)
    private UUID customerId;
}
