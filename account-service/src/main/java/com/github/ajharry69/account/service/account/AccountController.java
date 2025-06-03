package com.github.ajharry69.account.service.account;

import com.github.ajharry69.account.service.account.data.AccountFilter;
import com.github.ajharry69.account.service.account.models.dtos.AccountRequest;
import com.github.ajharry69.account.service.account.models.dtos.AccountResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts", description = "Operations related to accounts")
public class AccountController {
    private final AccountService service;
    private final PagedResourcesAssembler<AccountResponse> accountPageAssembler;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Get accounts")
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = {
                                    @Content(
                                            mediaType = MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE,
                                            schema = @Schema(implementation = Problem.class)
                                    )
                            }
                    )
            }
    )
    @PreAuthorize("hasAuthority('SCOPE_account.read')")
    public PagedModel<EntityModel<AccountResponse>> getAccounts(
            @ModelAttribute AccountFilter filter,
            Pageable pageable
    ) {
        Page<AccountResponse> accounts = service.getAccounts(pageable, filter);
        return accountPageAssembler.toModel(
                accounts,
                new AccountAssembler()
        );
    }

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Create account")
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successful creation",
                            headers = {
                                    @Header(name = "Location")
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request payload",
                            content = {
                                    @Content(
                                            mediaType = MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE,
                                            schema = @Schema(implementation = Problem.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = {
                                    @Content(
                                            mediaType = MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE,
                                            schema = @Schema(implementation = Problem.class)
                                    )
                            }
                    )
            }
    )
    @PreAuthorize("hasAuthority('SCOPE_account.create')")
    public ResponseEntity<EntityModel<AccountResponse>> createAccount(@RequestBody @Valid AccountRequest account) {
        AccountResponse response = service.createAccount(account);
        AccountAssembler assembler = new AccountAssembler();
        EntityModel<AccountResponse> model = assembler.toModel(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @GetMapping(value = "/{accountId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Get account by ID")
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Account not found",
                            content = {
                                    @Content(
                                            mediaType = MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE,
                                            schema = @Schema(implementation = Problem.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = {
                                    @Content(
                                            mediaType = MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE,
                                            schema = @Schema(implementation = Problem.class)
                                    )
                            }
                    )
            }
    )
    @PreAuthorize("hasAuthority('SCOPE_account.read')")
    public ResponseEntity<EntityModel<AccountResponse>> getAccount(
            @PathVariable
            UUID accountId) {
        AccountResponse response = service.getAccount(accountId);
        AccountAssembler assembler = new AccountAssembler();
        EntityModel<AccountResponse> model = assembler.toModel(response);
        return ResponseEntity.ok(model);
    }

    @PutMapping(value = "/{accountId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Update account by ID")
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful update"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Account not found",
                            content = {
                                    @Content(
                                            mediaType = MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE,
                                            schema = @Schema(implementation = Problem.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request payload",
                            content = {
                                    @Content(
                                            mediaType = MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE,
                                            schema = @Schema(implementation = Problem.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = {
                                    @Content(
                                            mediaType = MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE,
                                            schema = @Schema(implementation = Problem.class)
                                    )
                            }
                    )
            }
    )
    @PreAuthorize("hasAuthority('SCOPE_account.update')")
    public ResponseEntity<EntityModel<AccountResponse>> updateAccount(
            @PathVariable
            UUID accountId,
            @RequestBody @Valid AccountRequest account) {
        AccountResponse response = service.updateAccount(accountId, account);
        AccountAssembler assembler = new AccountAssembler();
        EntityModel<AccountResponse> model = assembler.toModel(response);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping(value = "/{accountId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Delete account by ID")
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successful deletion"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Account not found",
                            content = {
                                    @Content(
                                            mediaType = MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE,
                                            schema = @Schema(implementation = Problem.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = {
                                    @Content(
                                            mediaType = MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE,
                                            schema = @Schema(implementation = Problem.class)
                                    )
                            }
                    )
            }
    )
    @PreAuthorize("hasAuthority('SCOPE_account.delete')")
    public ResponseEntity<?> deleteAccount(
            @PathVariable
            UUID accountId) {
        service.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }
}
