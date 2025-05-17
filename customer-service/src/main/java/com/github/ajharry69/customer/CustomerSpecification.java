package com.github.ajharry69.customer;

import com.github.ajharry69.customer.models.Customer;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class CustomerSpecification implements Specification<Customer> {
    private final CustomerFilter filter;

    @Override
    public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.name() != null) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + filter.name().trim().toLowerCase() + "%"));
        }
        if (filter.startDateCreated() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dateCreated"), filter.startDateCreated()));
        }
        if (filter.endDateCreated() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dateCreated"), filter.endDateCreated()));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
