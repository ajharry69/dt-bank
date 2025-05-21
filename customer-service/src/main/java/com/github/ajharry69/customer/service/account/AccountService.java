package com.github.ajharry69.customer.service.account;

import com.github.ajharry69.customer.exceptions.CustomerNotFoundException;
import com.github.ajharry69.customer.service.account.data.AccountFilter;
import com.github.ajharry69.customer.service.account.models.AccountMapper;
import com.github.ajharry69.customer.service.account.models.dtos.AccountResponse;
import com.github.ajharry69.customer.service.account.models.dtos.CreateAccountRequest;
import com.github.ajharry69.customer.service.customer.data.CustomerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountMapper accountMapper;
    private final CustomerRepository repository;
    private final AccountClient accountClient;

    public EntityModel<AccountResponse> createAccount(UUID customerId, @Valid CreateAccountRequest request) {
        log.info("Creating account for customer with id: {} with request: {}", customerId, request);

        checkExistsByIdOrThrow(customerId);

        var accountRequest = accountMapper.toCreateAccountRequest(request);
        accountRequest.setCustomerId(customerId);
        var account = accountClient.createAccount(accountRequest);
        log.info("Created account: {} for customer: {}", account, customerId);
        return account;
    }

    public PagedModel<EntityModel<AccountResponse>> getAccounts(AccountFilter filter, Pageable pageable) {
        log.info("Getting accounts with filter: {}", filter);

        checkExistsByIdOrThrow(filter.getCustomerId());

        var page = accountClient.getAccounts(filter, pageable);
        if (page != null && page.getMetadata() != null) {
            log.info("Found {} accounts for customer: {}", page.getMetadata().getTotalElements(), filter.getCustomerId());
        }
        return page;
    }

    private void checkExistsByIdOrThrow(UUID customerId) {
        if (!repository.existsById(customerId)) {
            log.info("Customer with id: {} not found", customerId);
            throw new CustomerNotFoundException();
        }
    }
}
