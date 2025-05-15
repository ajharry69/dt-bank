package com.github.ajharry69.customer;

import com.github.ajharry69.customer.exceptions.CustomerNotFoundException;
import com.github.ajharry69.customer.models.Customer;
import com.github.ajharry69.customer.models.CustomerRequest;
import com.github.ajharry69.customer.models.CustomerResponse;
import com.github.ajharry69.customer.models.mappers.CustomerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CustomerService {
    private final CustomerMapper mapper;
    private final CustomerRepository repository;

    public Page<CustomerResponse> getCustomers(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponse);
    }

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        Customer entity = mapper.toEntity(request);
        Customer customer = repository.save(entity);
        return mapper.toResponse(customer);
    }

    public CustomerResponse getCustomer(UUID customerId) {
        Customer customer = repository.findById(customerId)
                .orElseThrow(CustomerNotFoundException::new);

        return mapper.toResponse(customer);
    }

    @Transactional
    public CustomerResponse updateCustomer(UUID customerId, CustomerRequest request) {
        if (!repository.existsById(customerId)) {
            throw new CustomerNotFoundException();
        }

        Customer entity = mapper.toEntity(request);
        entity.setId(customerId);
        Customer customer = repository.save(entity);
        return mapper.toResponse(customer);
    }

    @Transactional
    public void deleteCustomer(UUID customerId) {
        if (!repository.existsById(customerId)) {
            throw new CustomerNotFoundException();
        }

        repository.deleteById(customerId);
    }
}
