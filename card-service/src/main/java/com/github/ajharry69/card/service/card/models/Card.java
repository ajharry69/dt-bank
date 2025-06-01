package com.github.ajharry69.card.service.card.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity(name = "cards")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(value = AuditingEntityListener.class)
public class Card {
    @CreatedDate
    @Column(updatable = false)
    OffsetDateTime dateCreated;
    @LastModifiedDate
    @Column(insertable = false)
    OffsetDateTime dateLastModified;
    // Map the database-generated 'searchable' tsvector column.
    // It's read-only from the application's perspective.
    // The actual type 'Object' is used here as a placeholder;
    @Column(insertable = false, updatable = false)
    Object searchable;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String alias;
    @Column(nullable = false)
    private String pan;
    @Column(nullable = false)
    private String cvv;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CardType type;
    @Column(nullable = false)
    private UUID accountId;
}
