package com.github.ajharry69.customer;

import com.github.ajharry69.customer.models.CustomerRequest;
import com.github.ajharry69.customer.models.CustomerResponse;
import com.github.ajharry69.customer.utils.CustomerAssembler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/customers")
class CustomerController {
    private final CustomerService service;
    private final PagedResourcesAssembler<CustomerResponse> customerPageAssembler;

    @GetMapping
    public PagedModel<EntityModel<CustomerResponse>> getCustomers(
            @RequestParam(required = false)
            String name,
            @RequestParam(required = false)
            LocalDate startDateCreated,
            @RequestParam(required = false)
            LocalDate endDateCreated,
            Pageable pageable
    ) {
        var filter = CustomerFilter.builder()
                .name(name)
                .startDateCreated(startDateCreated)
                .endDateCreated(endDateCreated)
                .build();
        Page<CustomerResponse> customers = service.getCustomers(pageable, filter);
        return customerPageAssembler.toModel(
                customers,
                new CustomerAssembler()
        );
    }

    @PostMapping
    public ResponseEntity<EntityModel<CustomerResponse>> createCustomer(@RequestBody @Valid CustomerRequest customer) {
        CustomerResponse response = service.createCustomer(customer);
        CustomerAssembler assembler = new CustomerAssembler();
        EntityModel<CustomerResponse> model = assembler.toModel(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<EntityModel<CustomerResponse>> getCustomer(
            @PathVariable
            UUID customerId) {
        CustomerResponse response = service.getCustomer(customerId);
        CustomerAssembler assembler = new CustomerAssembler();
        EntityModel<CustomerResponse> model = assembler.toModel(response);
        return ResponseEntity.ok(model);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<EntityModel<CustomerResponse>> updateCustomer(
            @PathVariable
            UUID customerId,
            @RequestBody @Valid CustomerRequest customer) {
        CustomerResponse response = service.updateCustomer(customerId, customer);
        CustomerAssembler assembler = new CustomerAssembler();
        EntityModel<CustomerResponse> model = assembler.toModel(response);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<?> deleteCustomer(
            @PathVariable
            UUID customerId) {
        service.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}
