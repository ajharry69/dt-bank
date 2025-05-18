package com.github.ajharry69.account;

import com.github.ajharry69.account.exceptions.AccountNotFoundException;
import com.github.ajharry69.account.models.Account;
import com.github.ajharry69.account.models.AccountRequest;
import com.github.ajharry69.account.models.AccountResponse;
import com.github.ajharry69.account.models.mappers.AccountMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountMapper mapper;
    private final AccountRepository repository;

    public Page<AccountResponse> getAccounts(Pageable pageable, AccountFilter filter) {
        log.info("Getting accounts with filter: {}...", filter);
        var specification = new AccountSpecification(filter);
        Page<AccountResponse> page = repository.findAll(specification, pageable)
                .map(mapper::toResponse);
        log.info("Found {} accounts with filter: {}", page.getNumberOfElements(), filter);
        return page;
    }

    @Transactional
    public AccountResponse createAccount(AccountRequest request) {
        log.info("Creating account: {}", request);
        Account entity = mapper.toEntity(request);
        Account account = repository.save(entity);
        AccountResponse response = mapper.toResponse(account);
        log.info("Created account: {}", response);
        return response;
    }

    public AccountResponse getAccount(UUID accountId) {
        log.info("Getting account with id: {}", accountId);
        Account account = repository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);

        AccountResponse response = mapper.toResponse(account);
        log.info("Found account: {}", response);
        return response;
    }

    @Transactional
    public AccountResponse updateAccount(UUID accountId, AccountRequest request) {
        log.info("Updating account with id: {} with request: {}", accountId, request);
        checkExistsByIdOrThrow(accountId);

        Account entity = mapper.toEntity(request);
        entity.setId(accountId);
        Account account = repository.save(entity);
        AccountResponse response = mapper.toResponse(account);
        log.info("Updated account: {}", response);
        return response;
    }

    @Transactional
    public void deleteAccount(UUID accountId) {
        log.info("Deleting account with id: {}", accountId);
        checkExistsByIdOrThrow(accountId);

        repository.deleteById(accountId);
        log.info("Deleted account with id: {}", accountId);
    }

    private void checkExistsByIdOrThrow(UUID accountId) {
        if (!repository.existsById(accountId)) {
            log.info("Account with id: {} not found", accountId);
            throw new AccountNotFoundException();
        }
    }
}
