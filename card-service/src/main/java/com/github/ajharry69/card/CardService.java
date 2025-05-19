package com.github.ajharry69.card;

import com.github.ajharry69.card.exceptions.CardNotFoundException;
import com.github.ajharry69.card.exceptions.CardTypeAlreadyExistsException;
import com.github.ajharry69.card.models.Card;
import com.github.ajharry69.card.models.CardCreateRequest;
import com.github.ajharry69.card.models.CardResponse;
import com.github.ajharry69.card.models.CardUpdateRequest;
import com.github.ajharry69.card.models.mappers.CardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Service
public class CardService {
    private final CardMapper mapper;
    private final CardRepository repository;

    public Page<CardResponse> getCards(Pageable pageable, CardFilter filter) {
        log.info("Getting cards with filter: {}...", filter);
        var specification = new CardSpecification(filter);
        Function<Card, CardResponse> toResponse;
        if (filter.unmask()) {
            toResponse = mapper::toUnmaskedResponse;
        } else {
            toResponse = mapper::toResponse;
        }
        Page<CardResponse> page = repository.findAll(specification, pageable)
                .map(toResponse);
        log.info("Found {} cards with filter: {}", page.getNumberOfElements(), filter);
        return page;
    }

    @Transactional
    public CardResponse createCard(CardCreateRequest request) {
        log.info("Creating card: {}", request);
        if (repository.existsByAccountIdAndType(request.accountId(), request.type())) {
            log.error("Card type {} already exists for account with ID {}.", request.type(), request.accountId());
            throw new CardTypeAlreadyExistsException();
        }
        Card entity = mapper.toEntity(request);
        Card card = repository.save(entity);
        CardResponse response = mapper.toResponse(card);
        log.info("Created card: {}", response);
        return response;
    }

    public CardResponse getCard(UUID cardId, boolean unmask) {
        log.info("Getting card with id: {}", cardId);
        Card card = repository.findById(cardId)
                .orElseThrow(CardNotFoundException::new);

        CardResponse response;
        if (unmask) {
            response = mapper.toUnmaskedResponse(card);
        } else {
            response = mapper.toResponse(card);
        }
        log.info("Found card: {}", response);
        return response;
    }

    @Transactional
    public CardResponse updateCard(UUID cardId, CardUpdateRequest request) {
        log.info("Updating card with id: {} with request: {}", cardId, request);
        var entity = repository.findById(cardId)
                .orElseThrow(CardNotFoundException::new);
        entity.setAlias(request.alias());
        entity.setPan(request.pan());
        entity.setCvv(request.cvv());

        Card card = repository.save(entity);
        CardResponse response = mapper.toResponse(card);
        log.info("Updated card: {}", response);
        return response;
    }

    @Transactional
    public void deleteCard(UUID cardId) {
        log.info("Deleting card with id: {}", cardId);
        checkExistsByIdOrThrow(cardId);

        repository.deleteById(cardId);
        log.info("Deleted card with id: {}", cardId);
    }

    private void checkExistsByIdOrThrow(UUID cardId) {
        if (!repository.existsById(cardId)) {
            log.info("Card with id: {} not found", cardId);
            throw new CardNotFoundException();
        }
    }
}
