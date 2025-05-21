package com.github.ajharry69.card.service.card.data;

import com.github.ajharry69.card.service.card.models.Card;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class CardSpecification implements Specification<Card> {
    private final CardFilter filter;

    @Override
    public Predicate toPredicate(Root<Card> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getAccountId() != null) {
            predicates.add(cb.equal(root.get("accountId"), filter.getAccountId()));
        }
        if (filter.getAlias() != null && StringUtils.hasText(filter.getAlias())) {
            String searchTerm = filter.getAlias().trim();

            var tsQuery = cb.function(
                    "websearch_to_tsquery",
                    Object.class,
                    cb.literal(searchTerm)
            );

            predicates.add(
                    cb.isTrue(cb.function(
                            "ts_match_vq",
                            Boolean.class,
                            root.get("searchable"),
                            tsQuery
                    ))
            );

            var rankExpression = cb.function(
                    "ts_rank",
                    Double.class,
                    root.get("searchable"),
                    tsQuery
            );
            // Add ORDER BY rank DESC
            // query.orderBy(cb.desc(rankExpression));
            // It's generally better to let Pageable handle sorting if possible.
            // Since Pageable can't handle function-based sort, this is one way.
            // For Pageable to work with this, we might need to define "rank" as a sortable property
            // and have a custom query extension or a more complex setup.
            // For now, we'll add it directly to the query.
            // Check if the query is for a count query, if so, don't add orderBy
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                // Create a new list for order to avoid modifying existing orders if any
                List<Order> orders = new ArrayList<>();
                if (query.getOrderList() != null) {
                    orders.addAll(query.getOrderList());
                }
                orders.add(cb.desc(rankExpression));
                query.orderBy(orders);
            }
        }
        if (filter.getType() != null) {
            predicates.add(cb.equal(root.get("type"), filter.getType()));
        }
        if (filter.getPan() != null) {
            predicates.add(cb.equal(root.get("pan"), filter.getPan().trim()));
        }
        if (filter.getStartDateCreated() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("dateCreated"), filter.getStartDateCreated()));
        }
        if (filter.getEndDateCreated() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("dateCreated"), filter.getEndDateCreated()));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
