package com.github.ajharry69.card;

import com.github.ajharry69.card.models.Card;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class CardSpecification implements Specification<Card> {
    private final CardFilter filter;

    @Override
    public Predicate toPredicate(Root<Card> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.alias() != null) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("alias")), "%" + filter.alias().trim().toLowerCase() + "%"));
        }
        if (filter.type() != null) {
            predicates.add(criteriaBuilder.equal(root.get("type"), filter.type()));
        }
        if (filter.pan() != null) {
            predicates.add(criteriaBuilder.equal(root.get("pan"), filter.pan().trim()));
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
