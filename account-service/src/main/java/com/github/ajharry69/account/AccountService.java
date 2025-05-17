package com.github.ajharry69.account;

import com.github.ajharry69.account.exceptions.AccountNotFoundException;
import com.github.ajharry69.account.models.Account;
import com.github.ajharry69.account.models.AccountRequest;
import com.github.ajharry69.account.models.AccountResponse;
import com.github.ajharry69.account.models.mappers.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountMapper mapper;
    private final AccountRepository repository;

    public Page<AccountResponse> getAccounts(Pageable pageable, AccountFilter filter) {
        var specification = new AccountSpecification(filter);
        return repository.findAll(specification, pageable)
                .map(mapper::toResponse);
    }

    @Transactional
    public AccountResponse createAccount(AccountRequest request) {
        Account entity = mapper.toEntity(request);
        Account account = repository.save(entity);
        return mapper.toResponse(account);
    }

    public AccountResponse getAccount(UUID accountId) {
        Account account = repository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);

        return mapper.toResponse(account);
    }

    @Transactional
    public AccountResponse updateAccount(UUID accountId, AccountRequest request) {
        if (!repository.existsById(accountId)) {
            throw new AccountNotFoundException();
        }

        Account entity = mapper.toEntity(request);
        entity.setId(accountId);
        Account account = repository.save(entity);
        return mapper.toResponse(account);
    }

    @Transactional
    public void deleteAccount(UUID accountId) {
        if (!repository.existsById(accountId)) {
            throw new AccountNotFoundException();
        }

        repository.deleteById(accountId);
    }
}
