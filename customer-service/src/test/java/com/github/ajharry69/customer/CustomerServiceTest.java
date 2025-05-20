package com.github.ajharry69.customer;

import com.github.ajharry69.customer.data.CustomerFilter;
import com.github.ajharry69.customer.data.CustomerRepository;
import com.github.ajharry69.customer.data.CustomerSpecification;
import com.github.ajharry69.customer.exceptions.CustomerNotFoundException;
import com.github.ajharry69.customer.models.CreateAccountRequest;
import com.github.ajharry69.customer.models.Customer;
import com.github.ajharry69.customer.models.CustomerRequest;
import com.github.ajharry69.customer.models.CustomerResponse;
import com.github.ajharry69.customer.models.mappers.AccountMapper;
import com.github.ajharry69.customer.models.mappers.CustomerMapper;
import com.github.ajharry69.customer.service.account.AccountClient;
import com.github.ajharry69.customer.service.account.AccountFilter;
import com.github.ajharry69.customer.service.messaging.customer.CustomerMessagingService;
import com.github.ajharry69.customer.service.messaging.customer.CustomerDeletedEvent;
import net.datafaker.Faker;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceTest {
    private final Faker faker = new Faker();
    private final CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);
    private final AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);
    private final CustomerRepository repository = mock(CustomerRepository.class);
    private final AccountClient accountClient = mock(AccountClient.class);
    private final CustomerMessagingService customerMessagingService = mock(CustomerMessagingService.class);

    private CustomerService service;

    @BeforeEach
    public void setUp() {
        service = new CustomerService(customerMapper, accountMapper, repository, accountClient, customerMessagingService);
    }

    @Nested
    class GetCustomers {
        @Test
        void shouldReturnEmpty_WhenCustomersAreNotAvailable() {
            // Given
            when(repository.findAll(any(CustomerSpecification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));

            // When
            var actual = service.getCustomers(Pageable.unpaged(), CustomerFilter.builder().build());

            // Then
            assertAll(
                    () -> verify(repository, times(1))
                            .findAll(any(CustomerSpecification.class), any(Pageable.class)),
                    () -> assertIterableEquals(Collections.emptyList(), actual)
            );
        }

        @Test
        void shouldReturnNonEmpty_WhenCustomersAreAvailable() {
            // Given
            when(repository.findAll(any(CustomerSpecification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(Customer.builder().build())));

            // When
            var actual = service.getCustomers(Pageable.unpaged(), CustomerFilter.builder().build());

            // Then
            assertAll(
                    () -> verify(repository, times(1))
                            .findAll(any(CustomerSpecification.class), any(Pageable.class)),
                    () -> Assertions.assertThat(actual)
                            .isNotEmpty()
            );
        }
    }

    @Nested
    class GetCustomer {
        @Test
        void shouldThrowCustomerNotFoundException_IfCustomerIsNotAvailable() {
            // Given
            when(repository.findById(any()))
                    .thenReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> service.getCustomer(UUID.randomUUID()))
                    .isInstanceOf(CustomerNotFoundException.class);
        }

        @Test
        void shouldReturnCustomer_IfCustomerIsAvailable() {
            // Given
            when(repository.findById(any()))
                    .thenReturn(Optional.of(Customer.builder().build()));

            // When
            CustomerResponse customer = service.getCustomer(UUID.randomUUID());

            // Then
            assertThat(customer)
                    .isNotNull();
        }
    }

    @Nested
    class DeleteCustomer {
        @Test
        void shouldThrowCustomerNotFoundException_IfCustomerIsNotAvailable() {
            // Given
            when(repository.existsById(any()))
                    .thenReturn(false);

            // When
            assertAll(
                    () -> verify(repository, never()).save(any()),
                    () -> assertThatThrownBy(() -> service.deleteCustomer(UUID.randomUUID()))
                            .isInstanceOf(CustomerNotFoundException.class)
            );
        }

        @Test
        void shouldDelete_IfCustomerIsAvailable() {
            // Given
            when(repository.existsById(any()))
                    .thenReturn(true);

            // When
            UUID customerId = UUID.randomUUID();
            service.deleteCustomer(customerId);

            // Then
            var argumentCaptor = ArgumentCaptor.forClass(UUID.class);
            verify(repository, times(1))
                    .deleteById(argumentCaptor.capture());

            var id = argumentCaptor.getValue();
            assertThat(id)
                    .isEqualTo(customerId);

            var customerDeletedEventArgumentCaptor = ArgumentCaptor.forClass(CustomerDeletedEvent.class);
            verify(customerMessagingService, times(1))
                    .sendCustomerDeletedEvent(customerDeletedEventArgumentCaptor.capture());

            assertThat( customerDeletedEventArgumentCaptor.getValue().customerId())
                    .isEqualTo(customerId);
        }
    }

    @Nested
    class UpdateCustomer {
        @Test
        void shouldThrowCustomerNotFoundException_IfCustomerIsNotAvailable() {
            // Given
            when(repository.existsById(any()))
                    .thenReturn(false);

            // When
            assertAll(
                    () -> verify(repository, never()).save(any()),
                    () -> assertThatThrownBy(() -> {
                        CustomerRequest customer = CustomerRequest.builder()
                                .firstName("First")
                                .lastName("Last")
                                .build();
                        service.updateCustomer(UUID.randomUUID(), customer);
                    }).isInstanceOf(CustomerNotFoundException.class)
            );
        }

        @Test
        void shouldReturnCustomer_WhenCustomersIsAvailable() {
            // Given
            when(repository.existsById(any()))
                    .thenReturn(true);
            when(repository.save(any()))
                    .thenReturn(Customer.builder().id(UUID.randomUUID()).build());

            // When
            var actual = service.updateCustomer(
                    UUID.randomUUID(),
                    CustomerRequest.builder()
                            .firstName("First")
                            .lastName("Last")
                            .build()
            );

            // Then
            assertAll(
                    () -> {
                        var argumentCaptor = ArgumentCaptor.forClass(Customer.class);
                        verify(repository, times(1)).save(argumentCaptor.capture());

                        var entity = argumentCaptor.getValue();
                        assertThat(entity.getId())
                                .isNotNull();
                        assertThat(entity.getFirstName())
                                .isEqualTo("First");
                        assertThat(entity.getLastName())
                                .isEqualTo("Last");
                    },
                    () -> assertThat(actual.id())
                            .isNotNull()
            );
        }
    }

    @Nested
    class CreateCustomer {
        @Test
        void shouldReturnCustomer_WhenCustomersIsNotAvailable() {
            // Given
            when(repository.save(any()))
                    .thenReturn(Customer.builder().id(UUID.randomUUID()).build());

            // When
            var actual = service.createCustomer(
                    CustomerRequest.builder()
                            .firstName("First")
                            .lastName("Last")
                            .build()
            );

            // Then
            assertAll(
                    () -> {
                        var argumentCaptor = ArgumentCaptor.forClass(Customer.class);
                        verify(repository, times(1)).save(argumentCaptor.capture());

                        var entity = argumentCaptor.getValue();
                        assertThat(entity.getId())
                                .isNull();
                        assertThat(entity.getFirstName())
                                .isEqualTo("First");
                    },
                    () -> assertThat(actual.id())
                            .isNotNull()
            );
        }
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
            var argumentCaptor = ArgumentCaptor.forClass(com.github.ajharry69.customer.service.account.dtos.CreateAccountRequest.class);
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
            verify(accountClient, times(1)).getAccounts(
                    filter.customerId(),
                    filter.iban(),
                    filter.bicSwift(),
                    filter.startDateCreated(),
                    filter.endDateCreated(),
                    pageable
            );
        }
    }
}