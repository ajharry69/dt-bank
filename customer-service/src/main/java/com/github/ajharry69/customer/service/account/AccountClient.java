package com.github.ajharry69.customer.service.account;

import com.github.ajharry69.customer.service.account.data.AccountFilter;
import com.github.ajharry69.customer.service.account.models.dtos.AccountResponse;
import com.github.ajharry69.customer.service.account.models.dtos.CreateCustomerAccountRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "account-service",
        url = "${application.config.account-url:http://localhost:8080/api/v1/accounts}"
)
public interface AccountClient {

    @PostMapping
    EntityModel<AccountResponse> createAccount(@RequestBody CreateCustomerAccountRequest request);

    @GetMapping
    PagedModel<EntityModel<AccountResponse>> getAccounts(@SpringQueryMap AccountFilter filter, Pageable pageable);
}
