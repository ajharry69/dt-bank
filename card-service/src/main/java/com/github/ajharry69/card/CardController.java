package com.github.ajharry69.card;

import com.github.ajharry69.card.models.CardCreateRequest;
import com.github.ajharry69.card.models.CardResponse;
import com.github.ajharry69.card.models.CardType;
import com.github.ajharry69.card.models.CardUpdateRequest;
import com.github.ajharry69.card.utils.CardAssembler;
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

import java.time.LocalDate;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/cards")
class CardController {
    private final CardService service;
    private final PagedResourcesAssembler<CardResponse> cardPageAssembler;

    @GetMapping
    public PagedModel<EntityModel<CardResponse>> getCards(
            @RequestParam(required = false)
            String alias,
            @RequestParam(required = false)
            CardType type,
            @RequestParam(required = false)
            String pan,
            @RequestParam(required = false)
            LocalDate startDateCreated,
            @RequestParam(required = false)
            LocalDate endDateCreated,
            Pageable pageable
    ) {
        var filter = CardFilter.builder()
                .pan(pan)
                .type(type)
                .alias(alias)
                .startDateCreated(startDateCreated)
                .endDateCreated(endDateCreated)
                .build();
        Page<CardResponse> cards = service.getCards(pageable, filter);
        return cardPageAssembler.toModel(
                cards,
                new CardAssembler()
        );
    }

    @PostMapping
    public ResponseEntity<EntityModel<CardResponse>> createCard(@RequestBody @Valid CardCreateRequest card) {
        CardResponse response = service.createCard(card);
        CardAssembler assembler = new CardAssembler();
        EntityModel<CardResponse> model = assembler.toModel(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<EntityModel<CardResponse>> getCard(
            @PathVariable
            UUID cardId) {
        CardResponse response = service.getCard(cardId);
        CardAssembler assembler = new CardAssembler();
        EntityModel<CardResponse> model = assembler.toModel(response);
        return ResponseEntity.ok(model);
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<EntityModel<CardResponse>> updateCard(
            @PathVariable
            UUID cardId,
            @RequestBody @Valid CardUpdateRequest card) {
        CardResponse response = service.updateCard(cardId, card);
        CardAssembler assembler = new CardAssembler();
        EntityModel<CardResponse> model = assembler.toModel(response);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<?> deleteCard(
            @PathVariable
            UUID cardId) {
        service.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }
}
