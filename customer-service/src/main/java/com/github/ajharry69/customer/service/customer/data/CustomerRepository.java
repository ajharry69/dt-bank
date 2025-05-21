package com.github.ajharry69.customer.service.customer.data;

import com.github.ajharry69.customer.service.customer.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {
    @Transactional
    @Modifying
    @Query("update customers c set c.dateCreated = :dateCreated where c.id = :id")
    void updateDateCreatedById(@Param("dateCreated") OffsetDateTime dateCreated, @Param("id") UUID id);
}