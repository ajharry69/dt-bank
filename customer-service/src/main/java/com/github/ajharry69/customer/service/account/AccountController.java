package com.github.ajharry69.customer.service.account;

import com.github.ajharry69.SecuritySchemeName;
import com.github.ajharry69.customer.service.account.data.AccountFilter;
import com.github.ajharry69.customer.service.account.models.dtos.AccountResponse;
import com.github.ajharry69.customer.service.account.models.dtos.CreateAccountRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Accounts", description = "Operations related to accounts")
public class AccountController {
    private final AccountService service;

    @PostMapping(value = "/{customerId}/accounts", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Create account for customer")
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
    @SecurityRequirement(name = SecuritySchemeName.OAUTH2, scopes = {"account.create"})
    public ResponseEntity<EntityModel<AccountResponse>> createAccount(
            @PathVariable UUID customerId,
            @RequestBody @Valid CreateAccountRequest request) {
        EntityModel<AccountResponse> model = service.createAccount(customerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @GetMapping(value = "/{customerId}/accounts", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Get accounts for customer")
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
    @SecurityRequirement(name = SecuritySchemeName.OAUTH2, scopes = {"account.read"})
    public PagedModel<EntityModel<AccountResponse>> getAccounts(
            @PathVariable UUID customerId,
            @ModelAttribute AccountFilter filter,
            Pageable pageable) {
        filter.setCustomerId(customerId);
        return service.getAccounts(filter, pageable);
    }

}
