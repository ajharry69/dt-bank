package com.github.ajharry69.customer.service.customer;

import com.github.ajharry69.customer.exceptions.CustomerNotFoundException;
import com.github.ajharry69.customer.service.customer.data.CustomerFilter;
import com.github.ajharry69.customer.service.customer.data.CustomerRepository;
import com.github.ajharry69.customer.service.customer.data.CustomerSpecification;
import com.github.ajharry69.customer.service.customer.messaging.CustomerDeletedEvent;
import com.github.ajharry69.customer.service.customer.messaging.CustomerMessagingService;
import com.github.ajharry69.customer.service.customer.models.Customer;
import com.github.ajharry69.customer.service.customer.models.CustomerMapper;
import com.github.ajharry69.customer.service.customer.models.dtos.CustomerRequest;
import com.github.ajharry69.customer.service.customer.models.dtos.CustomerResponse;
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
    private final CustomerMapper customerMapper;
    private final CustomerRepository repository;
    private final CustomerMessagingService customerMessagingService;

    public Page<CustomerResponse> getCustomers(Pageable pageable, CustomerFilter filter) {
        log.info("Getting customers with filter: {}...", filter);
        var specification = new CustomerSpecification(filter);
        Page<CustomerResponse> page = repository.findAll(specification, pageable)
                .map(customerMapper::toResponse);
        log.info("Found {} customers with filter: {}", page.getNumberOfElements(), filter);
        return page;
    }

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        log.info("Creating customer: {}", request);
        Customer entity = customerMapper.toEntity(request);
        Customer customer = repository.save(entity);
        CustomerResponse response = customerMapper.toResponse(customer);
        log.info("Created customer: {}", response);
        return response;
    }

    public CustomerResponse getCustomer(UUID customerId) {
        log.info("Getting customer with id: {}", customerId);
        Customer customer = repository.findById(customerId)
                .orElseThrow(CustomerNotFoundException::new);

        CustomerResponse response = customerMapper.toResponse(customer);
        log.info("Found customer: {}", response);
        return response;
    }

    @Transactional
    public CustomerResponse updateCustomer(UUID customerId, CustomerRequest request) {
        log.info("Updating customer with id: {} with request: {}", customerId, request);
        checkExistsByIdOrThrow(customerId);

        Customer entity = customerMapper.toEntity(request);
        entity.setId(customerId);
        Customer customer = repository.save(entity);
        CustomerResponse response = customerMapper.toResponse(customer);
        log.info("Updated customer: {}", response);
        return response;
    }

    @Transactional
    public void deleteCustomer(UUID customerId) {
        log.info("Deleting customer with id: {}", customerId);
        checkExistsByIdOrThrow(customerId);

        repository.deleteById(customerId);
        customerMessagingService.sendCustomerDeletedEvent(new CustomerDeletedEvent(customerId));
        log.info("Deleted customer with id: {}", customerId);
    }

    private void checkExistsByIdOrThrow(UUID customerId) {
        if (!repository.existsById(customerId)) {
            log.info("Customer with id: {} not found", customerId);
            throw new CustomerNotFoundException();
        }
    }
}
