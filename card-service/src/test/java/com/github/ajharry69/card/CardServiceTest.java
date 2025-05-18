package com.github.ajharry69.card;

import com.github.ajharry69.card.exceptions.CardNotFoundException;
import com.github.ajharry69.card.exceptions.CardTypeAlreadyExistsException;
import com.github.ajharry69.card.models.Card;
import com.github.ajharry69.card.models.CardCreateRequest;
import com.github.ajharry69.card.models.CardResponse;
import com.github.ajharry69.card.models.CardUpdateRequest;
import com.github.ajharry69.card.models.mappers.CardMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CardServiceTest {
    private final CardMapper mapper = Mappers.getMapper(CardMapper.class);

    @Nested
    class GetCards {
        @Test
        void shouldReturnEmpty_WhenCardsAreNotAvailable() {
            // Given
            final var repository = mock(CardRepository.class);
            when(repository.findAll(any(CardSpecification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));
            var service = new CardService(mapper, repository);

            // When
            var actual = service.getCards(Pageable.unpaged(), CardFilter.builder().build());

            // Then
            assertAll(
                    () -> verify(repository, times(1))
                            .findAll(any(CardSpecification.class), any(Pageable.class)),
                    () -> assertIterableEquals(Collections.emptyList(), actual)
            );
        }

        @Test
        void shouldReturnNonEmpty_WhenCardsAreAvailable() {
            // Given
            var repository = mock(CardRepository.class);
            when(repository.findAll(any(CardSpecification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(Card.builder().build())));
            var service = new CardService(mapper, repository);

            // When
            var actual = service.getCards(Pageable.unpaged(), CardFilter.builder().build());

            // Then
            assertAll(
                    () -> verify(repository, times(1))
                            .findAll(any(CardSpecification.class), any(Pageable.class)),
                    () -> Assertions.assertThat(actual)
                            .isNotEmpty()
            );
        }
    }

    @Nested
    class GetCard {
        @Test
        void shouldThrowCardNotFoundException_IfCardIsNotAvailable() {
            // Given
            var repository = mock(CardRepository.class);
            when(repository.findById(any()))
                    .thenReturn(Optional.empty());
            var service = new CardService(mapper, repository);

            // When
            assertThatThrownBy(() -> service.getCard(UUID.randomUUID()))
                    .isInstanceOf(CardNotFoundException.class);
        }

        @Test
        void shouldReturnCard_IfCardIsAvailable() {
            // Given
            var repository = mock(CardRepository.class);
            when(repository.findById(any()))
                    .thenReturn(Optional.of(Card.builder().build()));
            var service = new CardService(mapper, repository);

            // When
            CardResponse card = service.getCard(UUID.randomUUID());

            // Then
            assertThat(card)
                    .isNotNull();
        }
    }

    @Nested
    class DeleteCard {
        @Test
        void shouldThrowCardNotFoundException_IfCardIsNotAvailable() {
            // Given
            var repository = mock(CardRepository.class);
            when(repository.existsById(any()))
                    .thenReturn(false);
            var service = new CardService(mapper, repository);

            // When
            assertAll(
                    () -> verify(repository, never()).save(any()),
                    () -> assertThatThrownBy(() -> service.deleteCard(UUID.randomUUID()))
                            .isInstanceOf(CardNotFoundException.class)
            );
        }

        @Test
        void shouldDelete_IfCardIsAvailable() {
            // Given
            var repository = mock(CardRepository.class);
            when(repository.existsById(any()))
                    .thenReturn(true);
            var service = new CardService(mapper, repository);

            // When
            UUID cardId = UUID.randomUUID();
            service.deleteCard(cardId);

            // Then
            var argumentCaptor = ArgumentCaptor.forClass(UUID.class);
            verify(repository, times(1)).deleteById(argumentCaptor.capture());

            var id = argumentCaptor.getValue();
            assertThat(id)
                    .isEqualTo(cardId);
        }
    }

    @Nested
    class UpdateCard {
        @Test
        void shouldThrowCardNotFoundException_IfCardIsNotAvailable() {
            // Given
            var repository = mock(CardRepository.class);
            when(repository.findById(any()))
                    .thenReturn(Optional.empty());
            var service = new CardService(mapper, repository);

            // When
            assertAll(
                    () -> verify(repository, never()).save(any()),
                    () -> assertThatThrownBy(() -> {
                        CardUpdateRequest card = CardUpdateRequest.builder()
                                .alias("First")
                                .pan("Last")
                                .build();
                        service.updateCard(UUID.randomUUID(), card);
                    }).isInstanceOf(CardNotFoundException.class)
            );
        }

        @Test
        void shouldReturnCard_WhenCardsIsAvailable() {
            // Given
            var repository = mock(CardRepository.class);
            when(repository.findById(any()))
                    .thenReturn(Optional.of(Card.builder().id(UUID.randomUUID()).build()));
            when(repository.save(any()))
                    .thenReturn(Card.builder().id(UUID.randomUUID()).build());
            var service = new CardService(mapper, repository);

            // When
            var actual = service.updateCard(
                    UUID.randomUUID(),
                    CardUpdateRequest.builder()
                            .alias("First")
                            .pan("Last")
                            .build()
            );

            // Then
            assertAll(
                    () -> {
                        var argumentCaptor = ArgumentCaptor.forClass(Card.class);
                        verify(repository, times(1)).save(argumentCaptor.capture());

                        var entity = argumentCaptor.getValue();
                        assertThat(entity.getId())
                                .isNotNull();
                        assertThat(entity.getAlias())
                                .isEqualTo("First");
                        assertThat(entity.getPan())
                                .isEqualTo("Last");
                    },
                    () -> assertThat(actual.id())
                            .isNotNull()
            );
        }
    }

    @Nested
    class CreateCard {
        @Test
        void shouldThrowErrorCardTypeAlreadyExistsException() {
            // Given
            var repository = mock(CardRepository.class);
            when(repository.existsByAccountIdAndType(any(), any()))
                    .thenReturn(true);
            var service = new CardService(mapper, repository);

            // When
            assertThatThrownBy(() -> service.createCard(
                    CardCreateRequest.builder()
                            .alias("First")
                            .pan("Last")
                            .build()
            )).isInstanceOf(CardTypeAlreadyExistsException.class);
        }

        @Test
        void shouldReturnCard_WhenCardsIsNotAvailable() {
            // Given
            var repository = mock(CardRepository.class);
            when(repository.existsByAccountIdAndType(any(), any()))
                    .thenReturn(false);
            when(repository.save(any()))
                    .thenReturn(Card.builder().id(UUID.randomUUID()).build());
            var service = new CardService(mapper, repository);

            // When
            var actual = service.createCard(
                    CardCreateRequest.builder()
                            .alias("First")
                            .pan("Last")
                            .build()
            );

            // Then
            assertAll(
                    () -> {
                        var argumentCaptor = ArgumentCaptor.forClass(Card.class);
                        verify(repository, times(1)).save(argumentCaptor.capture());

                        var entity = argumentCaptor.getValue();
                        assertThat(entity.getId())
                                .isNull();
                        assertThat(entity.getAlias())
                                .isEqualTo("First");
                    },
                    () -> assertThat(actual.id())
                            .isNotNull()
            );
        }
    }
}