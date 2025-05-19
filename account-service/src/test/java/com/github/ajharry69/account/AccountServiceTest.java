package com.github.ajharry69.account;

import com.github.ajharry69.account.exceptions.AccountNotFoundException;
import com.github.ajharry69.account.models.Account;
import com.github.ajharry69.account.models.AccountRequest;
import com.github.ajharry69.account.models.AccountResponse;
import com.github.ajharry69.account.models.mappers.AccountMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

class AccountServiceTest {
    private final AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);
    private final AccountRepository repository = mock(AccountRepository.class);

    private AccountService service;

    @BeforeEach
    public void setUp() {
        service = new AccountService(accountMapper, repository);
    }

    @Nested
    class GetAccounts {
        @Test
        void shouldReturnEmpty_WhenAccountsAreNotAvailable() {
            // Given
            when(repository.findAll(any(AccountSpecification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));

            // When
            var actual = service.getAccounts(Pageable.unpaged(), AccountFilter.builder().build());

            // Then
            assertAll(
                    () -> verify(repository, times(1))
                            .findAll(any(AccountSpecification.class), any(Pageable.class)),
                    () -> assertIterableEquals(Collections.emptyList(), actual)
            );
        }

        @Test
        void shouldReturnNonEmpty_WhenAccountsAreAvailable() {
            // Given
            when(repository.findAll(any(AccountSpecification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(Account.builder().build())));

            // When
            var actual = service.getAccounts(Pageable.unpaged(), AccountFilter.builder().build());

            // Then
            assertAll(
                    () -> verify(repository, times(1))
                            .findAll(any(AccountSpecification.class), any(Pageable.class)),
                    () -> Assertions.assertThat(actual)
                            .isNotEmpty()
            );
        }
    }

    @Nested
    class GetAccount {
        @Test
        void shouldThrowAccountNotFoundException_IfAccountIsNotAvailable() {
            // Given
            when(repository.findById(any()))
                    .thenReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> service.getAccount(UUID.randomUUID()))
                    .isInstanceOf(AccountNotFoundException.class);
        }

        @Test
        void shouldReturnAccount_IfAccountIsAvailable() {
            // Given
            when(repository.findById(any()))
                    .thenReturn(Optional.of(Account.builder().build()));

            // When
            AccountResponse account = service.getAccount(UUID.randomUUID());

            // Then
            assertThat(account)
                    .isNotNull();
        }
    }

    @Nested
    class DeleteAccount {
        @Test
        void shouldThrowAccountNotFoundException_IfAccountIsNotAvailable() {
            // Given
            when(repository.existsById(any()))
                    .thenReturn(false);

            // When
            assertAll(
                    () -> verify(repository, never()).save(any()),
                    () -> assertThatThrownBy(() -> service.deleteAccount(UUID.randomUUID()))
                            .isInstanceOf(AccountNotFoundException.class)
            );
        }

        @Test
        void shouldDelete_IfAccountIsAvailable() {
            // Given
            when(repository.existsById(any()))
                    .thenReturn(true);

            // When
            UUID accountId = UUID.randomUUID();
            service.deleteAccount(accountId);

            // Then
            var argumentCaptor = ArgumentCaptor.forClass(UUID.class);
            verify(repository, times(1)).deleteById(argumentCaptor.capture());

            var id = argumentCaptor.getValue();
            assertThat(id)
                    .isEqualTo(accountId);
        }
    }

    @Nested
    class UpdateAccount {
        @Test
        void shouldThrowAccountNotFoundException_IfAccountIsNotAvailable() {
            // Given
            when(repository.existsById(any()))
                    .thenReturn(false);

            // When
            assertAll(
                    () -> verify(repository, never()).save(any()),
                    () -> assertThatThrownBy(() -> {
                        AccountRequest account = AccountRequest.builder()
                                .iban("First")
                                .bicSwift("Last")
                                .build();
                        service.updateAccount(UUID.randomUUID(), account);
                    }).isInstanceOf(AccountNotFoundException.class)
            );
        }

        @Test
        void shouldReturnAccount_WhenAccountsIsAvailable() {
            // Given
            when(repository.existsById(any()))
                    .thenReturn(true);
            when(repository.save(any()))
                    .thenReturn(Account.builder().id(UUID.randomUUID()).build());

            // When
            var actual = service.updateAccount(
                    UUID.randomUUID(),
                    AccountRequest.builder()
                            .iban("First")
                            .bicSwift("Last")
                            .build()
            );

            // Then
            assertAll(
                    () -> {
                        var argumentCaptor = ArgumentCaptor.forClass(Account.class);
                        verify(repository, times(1)).save(argumentCaptor.capture());

                        var entity = argumentCaptor.getValue();
                        assertThat(entity.getId())
                                .isNotNull();
                        assertThat(entity.getIban())
                                .isEqualTo("First");
                        assertThat(entity.getBicSwift())
                                .isEqualTo("Last");
                    },
                    () -> assertThat(actual.id())
                            .isNotNull()
            );
        }
    }

    @Nested
    class CreateAccount {
        @Test
        void shouldReturnAccount_WhenAccountsIsNotAvailable() {
            // Given
            when(repository.save(any()))
                    .thenReturn(Account.builder().id(UUID.randomUUID()).build());

            // When
            var actual = service.createAccount(
                    AccountRequest.builder()
                            .iban("First")
                            .bicSwift("Last")
                            .build()
            );

            // Then
            assertAll(
                    () -> {
                        var argumentCaptor = ArgumentCaptor.forClass(Account.class);
                        verify(repository, times(1)).save(argumentCaptor.capture());

                        var entity = argumentCaptor.getValue();
                        assertThat(entity.getId())
                                .isNull();
                        assertThat(entity.getIban())
                                .isEqualTo("First");
                    },
                    () -> assertThat(actual.id())
                            .isNotNull()
            );
        }
    }
}