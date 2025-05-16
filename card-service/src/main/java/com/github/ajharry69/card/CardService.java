package com.github.ajharry69.card;

import com.github.ajharry69.card.exceptions.CardNotFoundException;
import com.github.ajharry69.card.models.Card;
import com.github.ajharry69.card.models.CardCreateRequest;
import com.github.ajharry69.card.models.CardResponse;
import com.github.ajharry69.card.models.CardUpdateRequest;
import com.github.ajharry69.card.models.mappers.CardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CardService {
    private final CardMapper mapper;
    private final CardRepository repository;

    public Page<CardResponse> getCards(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponse);
    }

    @Transactional
    public CardResponse createCard(CardCreateRequest request) {
        Card entity = mapper.toEntity(request);
        Card card = repository.save(entity);
        return mapper.toResponse(card);
    }

    public CardResponse getCard(UUID cardId) {
        Card card = repository.findById(cardId)
                .orElseThrow(CardNotFoundException::new);

        return mapper.toResponse(card);
    }

    @Transactional
    public CardResponse updateCard(UUID cardId, CardUpdateRequest request) {
        var entity = repository.findById(cardId)
                .orElseThrow(CardNotFoundException::new);
        entity.setAlias(request.alias());
        entity.setPan(request.pan());
        entity.setCvv(request.cvv());

        Card card = repository.save(entity);
        return mapper.toResponse(card);
    }

    @Transactional
    public void deleteCard(UUID cardId) {
        if (!repository.existsById(cardId)) {
            throw new CardNotFoundException();
        }

        repository.deleteById(cardId);
    }
}
