package com.github.ajharry69.customer;

import com.github.ajharry69.customer.exceptions.CustomerNotFoundException;
import com.github.ajharry69.customer.models.Customer;
import com.github.ajharry69.customer.models.CustomerRequest;
import com.github.ajharry69.customer.models.CustomerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository repository;

    @Override
    public Page<CustomerResponse> getCustomers(Pageable pageable) {
        return repository.findAll(pageable)
                .map(customer -> CustomerResponse.builder()
                        .id(customer.getId())
                        .firstName(customer.getFirstName())
                        .lastName(customer.getLastName())
                        .build());
    }

    @Override
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest customer) {
        Customer entity = Customer.builder()
                .firstName(customer.firstName())
                .lastName(customer.lastName())
                .build();
        Customer savedCustomer = repository.save(entity);
        return CustomerResponse.builder()
                .id(savedCustomer.getId())
                .firstName(savedCustomer.getFirstName())
                .lastName(savedCustomer.getLastName())
                .build();
    }

    @Override
    public CustomerResponse getCustomer(UUID customerId) {
        Customer customer = repository.findById(customerId)
                .orElseThrow(CustomerNotFoundException::new);

        return CustomerResponse.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .build();
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomer(UUID customerId, CustomerRequest customer) {
        if (!repository.existsById(customerId)) {
            throw new CustomerNotFoundException();
        }

        Customer entity = Customer.builder()
                .firstName(customer.firstName())
                .lastName(customer.lastName())
                .build();
        Customer saveCustomer = repository.save(entity);
        return CustomerResponse.builder()
                .id(saveCustomer.getId())
                .firstName(saveCustomer.getFirstName())
                .lastName(saveCustomer.getLastName())
                .build();
    }

    @Override
    @Transactional
    public void deleteCustomer(UUID customerId) {
        if (!repository.existsById(customerId)) {
            throw new CustomerNotFoundException();
        }

        repository.deleteById(customerId);
    }
}
