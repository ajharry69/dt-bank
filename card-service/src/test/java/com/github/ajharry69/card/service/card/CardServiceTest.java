package com.github.ajharry69.card.service.card;


import com.github.ajharry69.card.service.card.data.CardFilter;
import com.github.ajharry69.card.service.card.data.CardRepository;
import com.github.ajharry69.card.service.card.data.CardSpecification;
import com.github.ajharry69.card.exceptions.CardNotFoundException;
import com.github.ajharry69.card.exceptions.CardTypeAlreadyExistsException;
import com.github.ajharry69.card.service.card.models.Card;
import com.github.ajharry69.card.service.card.models.dtos.CreateCardRequest;
import com.github.ajharry69.card.service.card.models.dtos.UpdateCardRequest;
import com.github.ajharry69.card.service.card.models.CardMapper;
import net.datafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
    private static final Faker faker = new Faker();
    private final CardMapper cardMapper = Mappers.getMapper(CardMapper.class);
    private final CardRepository repository = mock(CardRepository.class);

    private CardService service;

    private static String pan() {
        return faker.finance().creditCard();
    }

    private static String cvv() {
        return faker.expression("#{numerify '###'}");
    }

    @BeforeEach
    public void setUp() {
        service = new CardService(cardMapper, repository);
    }

    @Nested
    class GetCards {
        @Test
        void shouldReturnEmpty_WhenCardsAreNotAvailable() {
            // Given
            when(repository.findAll(any(CardSpecification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));

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
            when(repository.findAll(any(CardSpecification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(Card.builder().build())));

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
        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void shouldThrowCardNotFoundException_IfCardIsNotAvailable(boolean unmask) {
            // Given
            when(repository.findById(any()))
                    .thenReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> service.getCard(UUID.randomUUID(), unmask))
                    .isInstanceOf(CardNotFoundException.class);
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void shouldReturnCard_IfCardIsAvailable(boolean unmask) {
            // Given
            var card = Card.builder()
                    .pan(pan())
                    .cvv(cvv())
                    .build();
            when(repository.findById(any()))
                    .thenReturn(Optional.of(card));

            // When
            var actual = service.getCard(UUID.randomUUID(), unmask);

            // Then
            if (unmask) {
                assertAll(
                        () -> assertThat(actual)
                                .isNotNull(),
                        () -> assertThat(actual.cvv())
                                .isEqualTo(card.getCvv()),
                        () -> assertThat(actual.pan())
                                .isEqualTo(card.getPan())

                );
            } else {
                assertAll(
                        () -> assertThat(actual)
                                .isNotNull(),
                        () -> assertThat(actual.cvv())
                                .isEqualTo("***"),
                        () -> assertThat(actual.pan())
                                .isEqualTo("*************")

                );
            }
        }
    }

    @Nested
    class DeleteCard {
        @Test
        void shouldThrowCardNotFoundException_IfCardIsNotAvailable() {
            // Given
            when(repository.existsById(any()))
                    .thenReturn(false);

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
            when(repository.existsById(any()))
                    .thenReturn(true);

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
            when(repository.findById(any()))
                    .thenReturn(Optional.empty());

            // When
            assertAll(
                    () -> verify(repository, never()).save(any()),
                    () -> assertThatThrownBy(() -> {
                        UpdateCardRequest card = UpdateCardRequest.builder()
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
            when(repository.findById(any()))
                    .thenReturn(Optional.of(Card.builder().id(UUID.randomUUID()).build()));
            when(repository.save(any()))
                    .thenReturn(Card.builder().id(UUID.randomUUID()).build());

            // When
            var actual = service.updateCard(
                    UUID.randomUUID(),
                    UpdateCardRequest.builder()
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
            when(repository.existsByAccountIdAndType(any(), any()))
                    .thenReturn(true);

            // When
            assertThatThrownBy(() -> service.createCard(
                    CreateCardRequest.builder()
                            .alias("First")
                            .pan("Last")
                            .build()
            )).isInstanceOf(CardTypeAlreadyExistsException.class);
        }

        @Test
        void shouldReturnCard_WhenCardsIsNotAvailable() {
            // Given
            when(repository.existsByAccountIdAndType(any(), any()))
                    .thenReturn(false);
            when(repository.save(any()))
                    .thenReturn(Card.builder().id(UUID.randomUUID()).build());

            // When
            var actual = service.createCard(
                    CreateCardRequest.builder()
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