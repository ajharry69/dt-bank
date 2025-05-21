package com.github.ajharry69.account.service.card;

import com.github.ajharry69.account.service.account.data.AccountRepository;
import com.github.ajharry69.account.exceptions.AccountNotFoundException;
import com.github.ajharry69.account.service.card.models.dtos.CreateCardRequest;
import com.github.ajharry69.account.service.card.models.CardMapper;
import com.github.ajharry69.account.service.card.data.CardFilter;
import com.github.ajharry69.account.service.card.models.dtos.CardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class CardService {
    private final CardMapper cardMapper;
    private final AccountRepository repository;
    private final CardClient cardClient;

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
        log.info("Getting cards with filter: {}", filter);

        checkExistsByIdOrThrow(filter.getAccountId());

        var page = cardClient.getCards(filter, pageable);
        if (page != null && page.getMetadata() != null) {
            log.info("Found {} cards for account: {}", page.getMetadata().getTotalElements(), filter.getAccountId());
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
