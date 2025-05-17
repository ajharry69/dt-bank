package com.github.ajharry69.customer;

import com.github.ajharry69.customer.exceptions.CustomerNotFoundException;
import com.github.ajharry69.customer.models.Customer;
import com.github.ajharry69.customer.models.CustomerRequest;
import com.github.ajharry69.customer.models.CustomerResponse;
import com.github.ajharry69.customer.models.mappers.CustomerMapper;
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

class CustomerServiceTest {
    private final CustomerMapper mapper = Mappers.getMapper(CustomerMapper.class);

    @Nested
    class GetCustomers {
        @Test
        void shouldReturnEmpty_WhenCustomersAreNotAvailable() {
            // Given
            final var repository = mock(CustomerRepository.class);
            when(repository.findAll(any(CustomerSpecification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));
            var service = new CustomerService(mapper, repository);

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
            var repository = mock(CustomerRepository.class);
            when(repository.findAll(any(CustomerSpecification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(Customer.builder().build())));
            var service = new CustomerService(mapper, repository);

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
            var repository = mock(CustomerRepository.class);
            when(repository.findById(any()))
                    .thenReturn(Optional.empty());
            var service = new CustomerService(mapper, repository);

            // When
            assertThatThrownBy(() -> service.getCustomer(UUID.randomUUID()))
                    .isInstanceOf(CustomerNotFoundException.class);
        }

        @Test
        void shouldReturnCustomer_IfCustomerIsAvailable() {
            // Given
            var repository = mock(CustomerRepository.class);
            when(repository.findById(any()))
                    .thenReturn(Optional.of(Customer.builder().build()));
            var service = new CustomerService(mapper, repository);

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
            var repository = mock(CustomerRepository.class);
            when(repository.existsById(any()))
                    .thenReturn(false);
            var service = new CustomerService(mapper, repository);

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
            var repository = mock(CustomerRepository.class);
            when(repository.existsById(any()))
                    .thenReturn(true);
            var service = new CustomerService(mapper, repository);

            // When
            UUID customerId = UUID.randomUUID();
            service.deleteCustomer(customerId);

            // Then
            var argumentCaptor = ArgumentCaptor.forClass(UUID.class);
            verify(repository, times(1)).deleteById(argumentCaptor.capture());

            var id = argumentCaptor.getValue();
            assertThat(id)
                    .isEqualTo(customerId);
        }
    }

    @Nested
    class UpdateCustomer {
        @Test
        void shouldThrowCustomerNotFoundException_IfCustomerIsNotAvailable() {
            // Given
            var repository = mock(CustomerRepository.class);
            when(repository.existsById(any()))
                    .thenReturn(false);
            var service = new CustomerService(mapper, repository);

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
            var repository = mock(CustomerRepository.class);
            when(repository.existsById(any()))
                    .thenReturn(true);
            when(repository.save(any()))
                    .thenReturn(Customer.builder().id(UUID.randomUUID()).build());
            var service = new CustomerService(mapper, repository);

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
            var repository = mock(CustomerRepository.class);
            when(repository.save(any()))
                    .thenReturn(Customer.builder().id(UUID.randomUUID()).build());
            var service = new CustomerService(mapper, repository);

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
}