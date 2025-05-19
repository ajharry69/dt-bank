package com.github.ajharry69.account;

import com.github.ajharry69.account.exceptions.AccountNotFoundException;
import com.github.ajharry69.account.models.Account;
import com.github.ajharry69.account.models.AccountRequest;
import com.github.ajharry69.account.models.AccountResponse;
import com.github.ajharry69.account.models.CreateCardRequest;
import com.github.ajharry69.account.models.mappers.AccountMapper;
import com.github.ajharry69.account.models.mappers.CardMapper;
import com.github.ajharry69.account.service.card.CardClient;
import com.github.ajharry69.account.service.card.CardFilter;
import com.github.ajharry69.account.service.card.dtos.CardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountMapper accountMapper;
    private final CardMapper cardMapper;
    private final AccountRepository repository;
    private final CardClient cardClient;

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
        log.info("Deleted account with id: {}", accountId);
    }

    public EntityModel<CardResponse> createCard(UUID accountId, @Valid CreateCardRequest request) {
        log.info("Creating card for account with id: {} with request: {}", accountId, request);

        checkExistsByIdOrThrow(accountId);

        var cardRequest = cardMapper.toCreateCardRequest(request);
        cardRequest.setAccountId(accountId);
        var card = cardClient.createCard(cardRequest);
        log.info("Created card: {} for account: {}", card, accountId);
        return card;
    }

    public PagedModel<EntityModel<CardResponse>> getCards(CardFilter filter, Pageable pageable) {
        log.info("Getting cards for account with id: {} with filter: {}", filter.accountId(), filter);

        checkExistsByIdOrThrow(filter.accountId());

        var page = cardClient.getCards(
                filter.accountId(),
                filter.alias(),
                filter.type(),
                filter.pan(),
                filter.startDateCreated(),
                filter.endDateCreated(),
                filter.unmask(),
                pageable
        );
        if (page != null) {
            log.info("Found {} cards for account: {}", page.getMetadata().getTotalElements(), filter.accountId());
        }
        return page;
    }

    private void checkExistsByIdOrThrow(UUID accountId) {
        if (!repository.existsById(accountId)) {
            log.info("Account with id: {} not found", accountId);
            throw new AccountNotFoundException();
        }
    }
}
