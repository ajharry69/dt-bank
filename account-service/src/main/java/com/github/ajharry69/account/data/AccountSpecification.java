package com.github.ajharry69.account.data;

import com.github.ajharry69.account.models.Account;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class AccountSpecification implements Specification<Account> {
    private final AccountFilter filter;

    @Override
    public Predicate toPredicate(Root<Account> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.customerId() != null) {
            predicates.add(criteriaBuilder.equal(root.get("customerId"), filter.customerId()));
        }
        if (filter.iban() != null) {
            predicates.add(criteriaBuilder.equal(root.get("iban"), filter.iban().trim()));
        }
        if (filter.bicSwift() != null) {
            predicates.add(criteriaBuilder.equal(root.get("bicSwift"), filter.bicSwift().trim()));
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
