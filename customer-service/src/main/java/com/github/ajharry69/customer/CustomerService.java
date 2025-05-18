package com.github.ajharry69.customer;

import com.github.ajharry69.customer.exceptions.CustomerNotFoundException;
import com.github.ajharry69.customer.models.Customer;
import com.github.ajharry69.customer.models.CustomerRequest;
import com.github.ajharry69.customer.models.CustomerResponse;
import com.github.ajharry69.customer.models.mappers.CustomerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerService {
    private final CustomerMapper mapper;
    private final CustomerRepository repository;

    public Page<CustomerResponse> getCustomers(Pageable pageable, CustomerFilter filter) {
        log.info("Getting customers with filter: {}...", filter);
        var specification = new CustomerSpecification(filter);
        Page<CustomerResponse> page = repository.findAll(specification, pageable)
                .map(mapper::toResponse);
        log.info("Found {} customers with filter: {}", page.getNumberOfElements(), filter);
        return page;
    }

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        log.info("Creating customer: {}", request);
        Customer entity = mapper.toEntity(request);
        Customer customer = repository.save(entity);
        CustomerResponse response = mapper.toResponse(customer);
        log.info("Created customer: {}", response);
        return response;
    }

    public CustomerResponse getCustomer(UUID customerId) {
        log.info("Getting customer with id: {}", customerId);
        Customer customer = repository.findById(customerId)
                .orElseThrow(CustomerNotFoundException::new);

        CustomerResponse response = mapper.toResponse(customer);
        log.info("Found customer: {}", response);
        return response;
    }

    @Transactional
    public CustomerResponse updateCustomer(UUID customerId, CustomerRequest request) {
        log.info("Updating customer with id: {} with request: {}", customerId, request);
        checkExistsByIdOrThrow(customerId);

        Customer entity = mapper.toEntity(request);
        entity.setId(customerId);
        Customer customer = repository.save(entity);
        CustomerResponse response = mapper.toResponse(customer);
        log.info("Updated customer: {}", response);
        return response;
    }

    @Transactional
    public void deleteCustomer(UUID customerId) {
        log.info("Deleting customer with id: {}", customerId);
        checkExistsByIdOrThrow(customerId);

        repository.deleteById(customerId);
        log.info("Deleted customer with id: {}", customerId);
    }

    private void checkExistsByIdOrThrow(UUID customerId) {
        if (!repository.existsById(customerId)) {
            log.info("Customer with id: {} not found", customerId);
            throw new CustomerNotFoundException();
        }
    }
}
