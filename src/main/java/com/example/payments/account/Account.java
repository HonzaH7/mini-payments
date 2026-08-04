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

    // Peníze NIKDY ne float/double (zaokrouhlovací chyby v binární plovoucí
    // desetinné čárce). BigDecimal + numeric(19,4) v DB = přesná desetinná
    // aritmetika. 19 platných číslic, 4 desetinná místa.
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    protected Account() {
        // JPA vyžaduje bezparametrový konstruktor.
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
