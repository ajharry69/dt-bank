package com.github.ajharry69.customer.service.account.data;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class AccountFilter {
    private UUID customerId;
    private String iban;
    private String bicSwift;
    private LocalDate startDateCreated;
    private LocalDate endDateCreated;
}