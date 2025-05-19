package com.github.ajharry69.customer.service.account;

import com.github.ajharry69.customer.service.account.dtos.AccountResponse;
import com.github.ajharry69.customer.service.account.dtos.CreateAccountRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.UUID;

@FeignClient(
        name = "account-service",
        url = "${application.config.account-url:http://localhost:8080/api/v1/accounts}"
)
public interface AccountClient {

    @PostMapping
    EntityModel<AccountResponse> createAccount(@RequestBody CreateAccountRequest request);

    @GetMapping
    PagedModel<EntityModel<AccountResponse>> getAccounts(
            @RequestParam(required = false)
            UUID customerId,
            @RequestParam(required = false)
            String iban,
            @RequestParam(required = false)
            String bicSwift,
            @RequestParam(required = false)
            LocalDate startDateCreated,
            @RequestParam(required = false)
            LocalDate endDateCreated,
            Pageable pageable
    );
}
