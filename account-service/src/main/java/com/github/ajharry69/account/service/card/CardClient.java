package com.github.ajharry69.account.service.card;

import com.github.ajharry69.account.models.CardType;
import com.github.ajharry69.account.service.card.dtos.CardResponse;
import com.github.ajharry69.account.service.card.dtos.CreateCardRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.UUID;

@FeignClient(
        name = "card-service",
        url = "${application.config.card-url:http://localhost:8080/api/v1/cards}"
)
public interface CardClient {

    @PostMapping
    EntityModel<CardResponse> createCard(@RequestBody CreateCardRequest request);

    @GetMapping
    PagedModel<EntityModel<CardResponse>> getCards(
            @RequestParam(required = false)
            UUID accountId,
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
            @RequestParam(required = false, defaultValue = "false")
            boolean unmask,
            Pageable pageable
    );
}
