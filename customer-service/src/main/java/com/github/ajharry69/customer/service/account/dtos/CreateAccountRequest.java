package com.github.ajharry69.customer.service.account.dtos;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest{
        String iban;
        String bicSwift;
        UUID customerId;
}
