package com.github.ajharry69.card.data;

import com.github.ajharry69.card.models.Card;
import com.github.ajharry69.card.models.CardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID>, JpaSpecificationExecutor<Card> {
    @Transactional
    @Modifying
    @Query("update cards c set c.dateCreated = :dateCreated where c.id = :id")
    void updateDateCreatedById(@Param("dateCreated") OffsetDateTime dateCreated, @Param("id") UUID id);

    boolean existsByAccountIdAndType(UUID accountId, CardType type);

    List<CardID> findByAccountId(UUID accountId);
}