package com.github.ajharry69.customer;

import com.github.ajharry69.customer.exceptions.CustomerNotFoundException;
import com.github.ajharry69.customer.models.CreateAccountRequest;
import com.github.ajharry69.customer.models.Customer;
import com.github.ajharry69.customer.models.CustomerRequest;
import com.github.ajharry69.customer.models.CustomerResponse;
import com.github.ajharry69.customer.models.mappers.AccountMapper;
import com.github.ajharry69.customer.models.mappers.CustomerMapper;
import com.github.ajharry69.customer.service.account.AccountClient;
import com.github.ajharry69.customer.service.account.AccountFilter;
import com.github.ajharry69.customer.service.account.dtos.AccountResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerService {
    private final CustomerMapper customerMapper;
    private final AccountMapper accountMapper;
    private final CustomerRepository repository;
    private final AccountClient accountClient;

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
        log.info("Deleted customer with id: {}", customerId);
    }

    public EntityModel<AccountResponse> createAccount(UUID customerId, @Valid CreateAccountRequest request) {
        log.info("Creating account for customer with id: {} with request: {}", customerId, request);

        checkExistsByIdOrThrow(customerId);

        var accountRequest = accountMapper.toCreateAccountRequest(request);
        accountRequest.setCustomerId(customerId);
        var account = accountClient.createAccount(accountRequest);
        log.info("Created account: {} for customer: {}", account, customerId);
        return account;
    }

    public PagedModel<EntityModel<AccountResponse>> getAccounts(AccountFilter filter, Pageable pageable) {
        log.info("Getting accounts for customer with id: {} with filter: {}", filter.customerId(), filter);

        checkExistsByIdOrThrow(filter.customerId());

        var page = accountClient.getAccounts(
                filter.customerId(),
                filter.iban(),
                filter.bicSwift(),
                filter.startDateCreated(),
                filter.endDateCreated(),
                pageable
        );
        if (page != null) {
            log.info("Found {} accounts for customer: {}", page.getMetadata().getTotalElements(), filter.customerId());
        }
        return page;
    }

    private void checkExistsByIdOrThrow(UUID customerId) {
        if (!repository.existsById(customerId)) {
            log.info("Customer with id: {} not found", customerId);
            throw new CustomerNotFoundException();
        }
    }
}
