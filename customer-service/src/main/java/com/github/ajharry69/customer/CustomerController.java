package com.github.ajharry69.customer;

import com.github.ajharry69.customer.models.CustomerRequest;
import com.github.ajharry69.customer.models.CustomerResponse;
import com.github.ajharry69.customer.utils.CustomerAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customers", description = "Operations related to customers")
class CustomerController {
    private final CustomerService service;
    private final PagedResourcesAssembler<CustomerResponse> customerPageAssembler;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Get customers")
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = {
                                    @Content(
                                            mediaType = MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE,
                                            schema = @Schema(implementation = Problem.class)
                                    )
                            }
                    )
            }
    )
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

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Create customer")
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successful creation",
                            headers = {
                                    @Header(name = "Location")
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request payload",
                            content = {
                                    @Content(
                                            mediaType = MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE,
                                            schema = @Schema(implementation = Problem.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = {
                                    @Content(
                                            mediaType = MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE,
                                            schema = @Schema(implementation = Problem.class)
                                    )
                            }
                    )
            }
    )
    public ResponseEntity<EntityModel<CustomerResponse>> createCustomer(@RequestBody @Valid CustomerRequest customer) {
        CustomerResponse response = service.createCustomer(customer);
        CustomerAssembler assembler = new CustomerAssembler();
        EntityModel<CustomerResponse> model = assembler.toModel(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @GetMapping(value = "/{customerId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Get customer by ID")
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Customer not found",
                            content = {
                                    @Content(
                                            mediaType = MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE,
                                            schema = @Schema(implementation = Problem.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = {
                                    @Content(
                                            mediaType = MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE,
                                            schema = @Schema(implementation = Problem.class)
                                    )
                            }
                    )
            }
    )
    public ResponseEntity<EntityModel<CustomerResponse>> getCustomer(
            @PathVariable
            UUID customerId) {
        CustomerResponse response = service.getCustomer(customerId);
        CustomerAssembler assembler = new CustomerAssembler();
        EntityModel<CustomerResponse> model = assembler.toModel(response);
        return ResponseEntity.ok(model);
    }

    @PutMapping(value = "/{customerId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Update customer by ID")
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful update"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Customer not found",
                            content = {
                                    @Content(
                                            mediaType = MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE,
                                            schema = @Schema(implementation = Problem.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request payload",
                            content = {
                                    @Content(
                                            mediaType = MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE,
                                            schema = @Schema(implementation = Problem.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = {
                                    @Content(
                                            mediaType = MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE,
                                            schema = @Schema(implementation = Problem.class)
                                    )
                            }
                    )
            }
    )
    public ResponseEntity<EntityModel<CustomerResponse>> updateCustomer(
            @PathVariable
            UUID customerId,
            @RequestBody @Valid CustomerRequest customer) {
        CustomerResponse response = service.updateCustomer(customerId, customer);
        CustomerAssembler assembler = new CustomerAssembler();
        EntityModel<CustomerResponse> model = assembler.toModel(response);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping(value = "/{customerId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Delete customer by ID")
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successful deletion"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Customer not found",
                            content = {
                                    @Content(
                                            mediaType = MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE,
                                            schema = @Schema(implementation = Problem.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = {
                                    @Content(
                                            mediaType = MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE,
                                            schema = @Schema(implementation = Problem.class)
                                    )
                            }
                    )
            }
    )
    public ResponseEntity<?> deleteCustomer(
            @PathVariable
            UUID customerId) {
        service.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}
