package com.github.ajharry69.account.service.account;

import com.github.ajharry69.account.service.account.data.AccountFilter;
import com.github.ajharry69.account.service.account.data.AccountRepository;
import com.github.ajharry69.account.service.account.data.AccountSpecification;
import com.github.ajharry69.account.exceptions.AccountNotFoundException;
import com.github.ajharry69.account.service.account.models.Account;
import com.github.ajharry69.account.service.account.models.dtos.AccountRequest;
import com.github.ajharry69.account.service.account.models.dtos.AccountResponse;
import com.github.ajharry69.account.service.account.models.AccountMapper;
import com.github.ajharry69.account.service.account.messaging.AccountDeletedEvent;
import com.github.ajharry69.account.service.account.messaging.AccountMessagingService;
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
    private final AccountMapper accountMapper;
    private final AccountRepository repository;
    private final AccountMessagingService accountMessagingService;

    public Page<AccountResponse> getAccounts(Pageable pageable, AccountFilter filter) {
        log.info("Getting accounts with filter: {}...", filter);
        var specification = new AccountSpecification(filter);
        Page<AccountResponse> page = repository.findAll(specification, pageable)
                .map(accountMapper::toResponse);
        log.info("Found {} accounts with filter: {}", page.getNumberOfElements(), filter);
        return page;
    }

    @Transactional
    public AccountResponse createAccount(AccountRequest request) {
        log.info("Creating account: {}", request);
        Account entity = accountMapper.toEntity(request);
        Account account = repository.save(entity);
        AccountResponse response = accountMapper.toResponse(account);
        log.info("Created account: {}", response);
        return response;
    }

    public AccountResponse getAccount(UUID accountId) {
        log.info("Getting account with id: {}", accountId);
        Account account = repository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);

        AccountResponse response = accountMapper.toResponse(account);
        log.info("Found account: {}", response);
        return response;
    }

    @Transactional
    public AccountResponse updateAccount(UUID accountId, AccountRequest request) {
        log.info("Updating account with id: {} with request: {}", accountId, request);
        checkExistsByIdOrThrow(accountId);

        Account entity = accountMapper.toEntity(request);
        entity.setId(accountId);
        Account account = repository.save(entity);
        AccountResponse response = accountMapper.toResponse(account);
        log.info("Updated account: {}", response);
        return response;
    }

    @Transactional
    public void deleteAccount(UUID accountId) {
        log.info("Deleting account with id: {}", accountId);
        checkExistsByIdOrThrow(accountId);

        repository.deleteById(accountId);
        accountMessagingService.sendAccountDeletedEvent(new AccountDeletedEvent(accountId));
        log.info("Deleted account with id: {}", accountId);
    }

    private void checkExistsByIdOrThrow(UUID accountId) {
        if (!repository.existsById(accountId)) {
            log.info("Account with id: {} not found", accountId);
            throw new AccountNotFoundException();
        }
    }
}
