package com.github.ajharry69.card.service.card;

import com.github.ajharry69.SecuritySchemeName;
import com.github.ajharry69.card.service.card.data.CardFilter;
import com.github.ajharry69.card.service.card.models.dtos.CardResponse;
import com.github.ajharry69.card.service.card.models.dtos.CreateCardRequest;
import com.github.ajharry69.card.service.card.models.dtos.UpdateCardRequest;
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
@RequestMapping("/api/v1/cards")
@Tag(name = "Cards", description = "Operations related to cards")
public class CardController {
    private final CardService service;
    private final PagedResourcesAssembler<CardResponse> cardPageAssembler;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Get cards")
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
    @PreAuthorize("hasAuthority('SCOPE_card.read')")
    @SecurityRequirement(name = SecuritySchemeName.OAUTH2, scopes = {"card.read"})
    public PagedModel<EntityModel<CardResponse>> getCards(
            @ModelAttribute CardFilter filter,
            Pageable pageable
    ) {
        var cards = service.getCards(pageable, filter);
        return cardPageAssembler.toModel(
                cards,
                new CardAssembler()
        );
    }

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Create card")
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
    @PreAuthorize("hasAuthority('SCOPE_card.create')")
    @SecurityRequirement(name = SecuritySchemeName.OAUTH2, scopes = {"card.create"})
    public ResponseEntity<EntityModel<CardResponse>> createCard(@RequestBody @Valid CreateCardRequest card) {
        CardResponse response = service.createCard(card);
        CardAssembler assembler = new CardAssembler();
        EntityModel<CardResponse> model = assembler.toModel(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @GetMapping(value = "/{cardId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Get card by ID")
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Card not found",
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
    @PreAuthorize("hasAuthority('SCOPE_card.read')")
    @SecurityRequirement(name = SecuritySchemeName.OAUTH2, scopes = {"card.read"})
    public ResponseEntity<EntityModel<CardResponse>> getCard(
            @PathVariable
            UUID cardId,
            @RequestParam(required = false, defaultValue = "false")
            boolean unmask
    ) {
        CardResponse response = service.getCard(cardId, unmask);
        CardAssembler assembler = new CardAssembler();
        EntityModel<CardResponse> model = assembler.toModel(response);
        return ResponseEntity.ok(model);
    }

    @PutMapping(value = "/{cardId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Update card by ID")
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful update"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Card not found",
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
    @PreAuthorize("hasAuthority('SCOPE_card.update')")
    @SecurityRequirement(name = SecuritySchemeName.OAUTH2, scopes = {"card.update"})
    public ResponseEntity<EntityModel<CardResponse>> updateCard(
            @PathVariable
            UUID cardId,
            @RequestBody @Valid UpdateCardRequest card) {
        CardResponse response = service.updateCard(cardId, card);
        CardAssembler assembler = new CardAssembler();
        EntityModel<CardResponse> model = assembler.toModel(response);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping(value = "/{cardId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Delete card by ID")
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successful deletion"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Card not found",
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
    @PreAuthorize("hasAuthority('SCOPE_card.delete')")
    @SecurityRequirement(name = SecuritySchemeName.OAUTH2, scopes = {"card.delete"})
    public ResponseEntity<?> deleteCard(
            @PathVariable
            UUID cardId) {
        service.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }
}
