package com.github.ajharry69.customer.service.account;

import com.github.ajharry69.customer.service.account.data.AccountFilter;
import com.github.ajharry69.customer.service.account.models.dtos.CreateCustomerAccountRequest;
import com.github.ajharry69.customer.service.customer.data.CustomerRepository;
import com.github.ajharry69.customer.exceptions.CustomerNotFoundException;
import com.github.ajharry69.customer.service.account.models.dtos.CreateAccountRequest;
import com.github.ajharry69.customer.service.account.models.AccountMapper;
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

class AccountServiceTest {
    private final Faker faker = new Faker();
    private final AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);
    private final CustomerRepository repository = mock(CustomerRepository.class);
    private final AccountClient accountClient = mock(AccountClient.class);

    private AccountService service;

    @BeforeEach
    public void setUp() {
        service = new AccountService(accountMapper, repository, accountClient);
    }

    @Nested
    class CreateAccount {
        @Test
        void shouldThrowCustomerNotFoundException() {
            when(repository.existsById(any()))
                    .thenReturn(false);

            assertThrows(
                    CustomerNotFoundException.class,
                    () -> service.createAccount(
                            UUID.randomUUID(),
                            CreateAccountRequest.builder()
                                    .iban(faker.finance().iban())
                                    .bicSwift(faker.finance().bic())
                                    .build()
                    )
            );
        }

        @Test
        void shouldReturnAccount_WhenAccountsIsNotAvailable() {
            // Given
            var customerId = UUID.randomUUID();
            var request = CreateAccountRequest.builder()
                    .iban(faker.finance().iban())
                    .bicSwift(faker.finance().bic())
                    .build();
            when(repository.existsById(any()))
                    .thenReturn(true);

            // When
            service.createAccount(customerId, request);

            // Then
            var argumentCaptor = ArgumentCaptor.forClass(CreateCustomerAccountRequest.class);
            verify(accountClient, times(1)).createAccount(argumentCaptor.capture());

            var entity = argumentCaptor.getValue();
            assertAll(
                    () -> assertThat(entity.getCustomerId())
                            .isEqualTo(customerId),
                    () -> assertThat(entity.getIban())
                            .isEqualTo(request.iban()),
                    () -> assertThat(entity.getBicSwift())
                            .isEqualTo(request.bicSwift())
            );
        }
    }

    @Nested
    class GetAccounts {
        @Test
        void shouldThrowCustomerNotFoundException() {
            when(repository.existsById(any()))
                    .thenReturn(false);

            assertThrows(
                    CustomerNotFoundException.class,
                    () -> service.getAccounts(
                            AccountFilter.builder()
                                    .customerId(UUID.randomUUID())
                                    .build(),
                            Pageable.unpaged()
                    )
            );
        }

        @Test
        void shouldReturnAccounts() {
            // Given
            Pageable pageable = Pageable.unpaged();
            var filter = AccountFilter.builder()
                    .customerId(UUID.randomUUID())
                    .build();
            when(repository.existsById(any()))
                    .thenReturn(true);

            // When
            service.getAccounts(filter, pageable);

            // Then
            verify(accountClient, times(1))
                    .getAccounts(filter, pageable);
        }
    }
}