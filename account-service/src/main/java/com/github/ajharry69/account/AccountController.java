package com.github.ajharry69.account;

import com.github.ajharry69.account.AccountService;
import com.github.ajharry69.account.models.AccountRequest;
import com.github.ajharry69.account.models.AccountResponse;
import com.github.ajharry69.account.utils.AccountAssembler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/accounts")
class AccountController {
    private final AccountService service;
    private final PagedResourcesAssembler<AccountResponse> accountPageAssembler;

    @GetMapping
    public PagedModel<EntityModel<AccountResponse>> getAccounts(Pageable pageable) {
        Page<AccountResponse> accounts = service.getAccounts(pageable);
        return accountPageAssembler.toModel(
                accounts,
                new AccountAssembler()
        );
    }

    @PostMapping
    public ResponseEntity<EntityModel<AccountResponse>> createAccount(@RequestBody @Valid AccountRequest account) {
        AccountResponse response = service.createAccount(account);
        AccountAssembler assembler = new AccountAssembler();
        EntityModel<AccountResponse> model = assembler.toModel(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<EntityModel<AccountResponse>> getAccount(
            @PathVariable
            UUID accountId) {
        AccountResponse response = service.getAccount(accountId);
        AccountAssembler assembler = new AccountAssembler();
        EntityModel<AccountResponse> model = assembler.toModel(response);
        return ResponseEntity.ok(model);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<EntityModel<AccountResponse>> updateAccount(
            @PathVariable
            UUID accountId,
            @RequestBody @Valid AccountRequest account) {
        AccountResponse response = service.updateAccount(accountId, account);
        AccountAssembler assembler = new AccountAssembler();
        EntityModel<AccountResponse> model = assembler.toModel(response);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<?> deleteAccount(
            @PathVariable
            UUID accountId) {
        service.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }
}
