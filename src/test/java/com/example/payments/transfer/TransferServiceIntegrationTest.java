package com.example.payments.transfer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.payments.AbstractIntegrationTest;
import com.example.payments.account.Account;
import com.example.payments.account.AccountRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TransferServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    TransferService transferService;

    @Autowired
    AccountRepository accounts;

    Long aliceId;
    Long bobId;

    @BeforeEach
    void setUp() {
        accounts.deleteAll();
        aliceId = accounts.save(new Account("alice", new BigDecimal("1000.00"))).getId();
        bobId = accounts.save(new Account("bob", new BigDecimal("500.00"))).getId();
    }

    @Test
    void successfulTransferMovesMoney() {
        transferService.transfer(UUID.randomUUID().toString(), aliceId, bobId, new BigDecimal("100.00"));

        assertThat(accounts.findById(aliceId).orElseThrow().getBalance()).isEqualByComparingTo("900.00");
        assertThat(accounts.findById(bobId).orElseThrow().getBalance()).isEqualByComparingTo("600.00");
    }

    @Test
    void insufficientFundsRollsBackBoth() {
        assertThatThrownBy(() -> transferService.transfer(
                UUID.randomUUID().toString(), aliceId, bobId, new BigDecimal("5000.00")))
                .isInstanceOf(InsufficientFundsException.class);

        assertThat(accounts.findById(aliceId).orElseThrow().getBalance()).isEqualByComparingTo("1000.00");
        assertThat(accounts.findById(bobId).orElseThrow().getBalance()).isEqualByComparingTo("500.00");
    }

    @Test
    void sameIdempotencyKeyTransfersOnlyOnce() {
        String key = "test-key-1";
        transferService.transfer(key, aliceId, bobId, new BigDecimal("100.00"));
        transferService.transfer(key, aliceId, bobId, new BigDecimal("100.00"));

        assertThat(accounts.findById(aliceId).orElseThrow().getBalance()).isEqualByComparingTo("900.00");
        assertThat(accounts.findById(bobId).orElseThrow().getBalance()).isEqualByComparingTo("600.00");
    }
}