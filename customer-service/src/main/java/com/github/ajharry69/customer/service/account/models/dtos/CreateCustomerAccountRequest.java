package com.github.ajharry69.customer.service.account.models.dtos;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerAccountRequest {
    String iban;
    String bicSwift;
    UUID customerId;
}
