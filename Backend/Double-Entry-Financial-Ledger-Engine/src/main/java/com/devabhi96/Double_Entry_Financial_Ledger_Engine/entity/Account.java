package com.devabhi96.Double_Entry_Financial_Ledger_Engine.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable =false, precision = 19, scale = 4 )
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(name = "created_at",nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name ="updated_at",nullable = false)
    private Instant updatedAt = Instant.now();

    protected Account() {}

    public Account(Long userId) {
        this.userId = userId;
    }

    public UUID getId() { return id; }
    public Long getUserId() { return userId; }
    public BigDecimal getBalance() { return balance; }
    public AccountStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
