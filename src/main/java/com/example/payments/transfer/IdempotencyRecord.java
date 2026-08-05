package com.example.payments.transfer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "idempotency_record",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_idempotency_key",
                columnNames = "idempotency_key"))
public class IdempotencyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Klíč od klienta. UNIQUE (viz @Table výše) = databáze nedovolí dva stejné.
    @Column(name = "idempotency_key", nullable = false)
    private String idempotencyKey;

    // Uložený výsledek původního převodu - vrátíme ho při opakování stejného klíče.
    @Column(nullable = false)
    private Long fromAccount;

    @Column(nullable = false)
    private Long toAccount;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal fromBalance;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal toBalance;

    @Column(nullable = false)
    private Instant createdAt;

    protected IdempotencyRecord() {
        // JPA
    }

    public IdempotencyRecord(
            String idempotencyKey,
            Long fromAccount,
            Long toAccount,
            BigDecimal amount,
            BigDecimal fromBalance,
            BigDecimal toBalance) {
        this.idempotencyKey = idempotencyKey;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.fromBalance = fromBalance;
        this.toBalance = toBalance;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public Long getFromAccount() {
        return fromAccount;
    }

    public Long getToAccount() {
        return toAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getFromBalance() {
        return fromBalance;
    }

    public BigDecimal getToBalance() {
        return toBalance;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}