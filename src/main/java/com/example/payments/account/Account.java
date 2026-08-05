package com.example.payments.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String owner;

    // Money is NEVER float/double (rounding errors in binary floating point).
    // BigDecimal + numeric(19,4) in the DB = exact decimal arithmetic.
    // 19 significant digits, 4 decimal places.
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    protected Account() {
        // JPA requires a no-argument constructor.
    }

    public Account(String owner, BigDecimal balance) {
        this.owner = owner;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
