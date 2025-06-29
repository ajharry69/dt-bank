package com.github.ajharry69.account.service.card;

import com.github.ajharry69.SecuritySchemeName;
import com.github.ajharry69.account.service.card.data.CardFilter;
import com.github.ajharry69.account.service.card.models.dtos.CardResponse;
import com.github.ajharry69.account.service.card.models.dtos.CreateCardRequest;
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
@RequestMapping("/api/v1/accounts")
@Tag(name = "Cards", description = "Operations related to cards")
public class CardController {
    private final CardService service;

    @PostMapping(value = "/{accountId}/cards", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Create card for account")
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
    @SecurityRequirement(name = SecuritySchemeName.OAUTH2, scopes = {"card.create"})
    public ResponseEntity<EntityModel<CardResponse>> createCard(
            @PathVariable UUID accountId,
            @RequestBody @Valid CreateCardRequest request) {
        EntityModel<CardResponse> model = service.createCard(accountId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @GetMapping(value = "/{accountId}/cards", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Get cards for account")
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
    @SecurityRequirement(name = SecuritySchemeName.OAUTH2, scopes = {"card.read"})
    public PagedModel<EntityModel<CardResponse>> getCards(
            @PathVariable UUID accountId,
            @ModelAttribute CardFilter filter,
            Pageable pageable) {
        filter.setAccountId(accountId);
        return service.getCards(filter, pageable);
    }
}
