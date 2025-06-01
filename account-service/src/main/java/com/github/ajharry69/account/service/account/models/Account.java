package com.github.ajharry69.account.service.account.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity(name = "accounts")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(value = AuditingEntityListener.class)
public class Account {
    @CreatedDate
    @Column(updatable = false)
    OffsetDateTime dateCreated;
    @LastModifiedDate
    @Column(insertable = false)
    OffsetDateTime dateLastModified;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String iban;
    @Column(nullable = false)
    private String bicSwift;
    @Column(nullable = false)
    private UUID customerId;
}
