package com.github.ajharry69.customer;

import com.github.ajharry69.customer.models.CustomerRequest;
import com.github.ajharry69.customer.models.CustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CustomerService {
    Page<CustomerResponse> getCustomers(Pageable pageable);

    CustomerResponse createCustomer(CustomerRequest customer);

    CustomerResponse getCustomer(UUID customerId);

    CustomerResponse updateCustomer(UUID customerId, CustomerRequest customer);

    void deleteCustomer(UUID customerId);
}
