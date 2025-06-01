package com.github.ajharry69.account.service.card;

import com.github.ajharry69.account.service.card.data.CardFilter;
import com.github.ajharry69.account.service.card.models.dtos.CardResponse;
import com.github.ajharry69.account.service.card.models.dtos.CreateAccountCardRequest;
import com.github.ajharry69.autoconfigure.DTBFeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "card-service",
        url = "${application.config.gateway.url:http://localhost:8080}/api/v1/cards",
        configuration = {DTBFeignClientConfig.class}
)
public interface CardClient {

    @PostMapping
    EntityModel<CardResponse> createCard(@RequestBody CreateAccountCardRequest request);

    @GetMapping
    PagedModel<EntityModel<CardResponse>> getCards(@SpringQueryMap CardFilter filter, Pageable pageable);
}
