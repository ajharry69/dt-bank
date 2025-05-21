package com.github.ajharry69.account.service.card;

import com.github.ajharry69.account.service.account.data.AccountRepository;
import com.github.ajharry69.account.exceptions.AccountNotFoundException;
import com.github.ajharry69.account.service.card.models.CardType;
import com.github.ajharry69.account.service.card.models.dtos.CreateCardRequest;
import com.github.ajharry69.account.service.card.models.CardMapper;
import com.github.ajharry69.account.service.card.data.CardFilter;
import com.github.ajharry69.account.service.card.models.dtos.CreateAccountCardRequest;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CardServiceTest {
    private static final Faker faker = new Faker();
    private final CardMapper cardMapper = Mappers.getMapper(CardMapper.class);
    private final AccountRepository repository = mock(AccountRepository.class);
    private final CardClient cardClient = mock(CardClient.class);

    private CardService service;

    private static String alias() {
        return faker.funnyName().name();
    }

    private static String pan() {
        return faker.finance().creditCard();
    }

    private static String cvv() {
        return faker.expression("#{numerify '###'}");
    }

    private static CardType type() {
        return faker.options().option(CardType.class);
    }

    @BeforeEach
    public void setUp() {
        service = new CardService(cardMapper, repository, cardClient);
    }

    @Nested
    class CreateCard {
        @Test
        void shouldThrowAccountNotFoundException() {
            when(repository.existsById(any()))
                    .thenReturn(false);

            assertThrows(
                    AccountNotFoundException.class,
                    () -> service.createCard(
                            UUID.randomUUID(),
                            CreateCardRequest.builder()
                                    .alias(alias())
                                    .pan(pan())
                                    .cvv(cvv())
                                    .type(type())
                                    .build()
                    )
            );
        }

        @Test
        void shouldReturnCard_WhenCardsIsNotAvailable() {
            // Given
            var accountId = UUID.randomUUID();
            var request = CreateCardRequest.builder()
                    .alias(alias())
                    .pan(pan())
                    .cvv(cvv())
                    .type(type())
                    .build();
            when(repository.existsById(any()))
                    .thenReturn(true);

            // When
            service.createCard(accountId, request);

            // Then
            var argumentCaptor = ArgumentCaptor.forClass(CreateAccountCardRequest.class);
            verify(cardClient, times(1)).createCard(argumentCaptor.capture());

            var entity = argumentCaptor.getValue();
            assertAll(
                    () -> assertThat(entity.getAccountId())
                            .isEqualTo(accountId),
                    () -> assertThat(entity.getAlias())
                            .isEqualTo(request.alias()),
                    () -> assertThat(entity.getPan())
                            .isEqualTo(request.pan()),
                    () -> assertThat(entity.getCvv())
                            .isEqualTo(request.cvv()),
                    () -> assertThat(entity.getType())
                            .isEqualTo(request.type())
            );
        }
    }

    @Nested
    class GetCards {
        @Test
        void shouldThrowAccountNotFoundException() {
            when(repository.existsById(any()))
                    .thenReturn(false);

            assertThrows(
                    AccountNotFoundException.class,
                    () -> service.getCards(
                            CardFilter.builder()
                                    .accountId(UUID.randomUUID())
                                    .build(),
                            Pageable.unpaged()
                    )
            );
        }

        @Test
        void shouldReturnCards() {
            // Given
            Pageable pageable = Pageable.unpaged();
            var filter = CardFilter.builder()
                    .accountId(UUID.randomUUID())
                    .build();
            when(repository.existsById(any()))
                    .thenReturn(true);

            // When
            service.getCards(filter, pageable);

            // Then
            verify(cardClient, times(1))
                    .getCards(filter, pageable);
        }
    }
}