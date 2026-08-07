package com.example.payments.transfer;

import com.example.payments.account.Account;
import com.example.payments.account.AccountRepository;
import com.example.payments.messaging.TransferCompletedEvent;
import java.math.BigDecimal;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    private final AccountRepository accounts;
    private final IdempotencyRecordRepository idempotencyRecords;
    private final ApplicationEventPublisher events;

    public TransferService(
            AccountRepository accounts,
            IdempotencyRecordRepository idempotencyRecords,
            ApplicationEventPublisher events) {
        this.accounts = accounts;
        this.idempotencyRecords = idempotencyRecords;
        this.events = events;
    }

    @Transactional
    public TransferResponse transfer(String idempotencyKey, Long fromId, Long toId, BigDecimal amount) {
        Optional<IdempotencyRecord> existing = idempotencyRecords.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            IdempotencyRecord rec = existing.get();
            log.info("Duplicate Idempotency-Key {} -> returning original result, NO transfer", idempotencyKey);
            return new TransferResponse(
                    rec.getFromAccount(), rec.getToAccount(), rec.getAmount(),
                    rec.getFromBalance(), rec.getToBalance());
        }

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }

        Account from = accounts.findById(fromId)
                .orElseThrow(() -> new IllegalArgumentException("Account does not exist: " + fromId));
        Account to = accounts.findById(toId)
                .orElseThrow(() -> new IllegalArgumentException("Account does not exist: " + toId));

        from.setBalance(from.getBalance().subtract(amount));
        log.info("Debited {} from account {} -> balance IN MEMORY {}", amount, fromId, from.getBalance());

        to.setBalance(to.getBalance().add(amount));
        log.info("Credited {} to account {} -> balance IN MEMORY {}", amount, toId, to.getBalance());

        if (from.getBalance().signum() < 0) {
            throw new InsufficientFundsException(
                    "Insufficient funds in account " + fromId + " for transfer " + amount);
        }

        idempotencyRecords.save(new IdempotencyRecord(
                idempotencyKey, fromId, toId, amount, from.getBalance(), to.getBalance()));

        // Vyhlásíme Spring událost. Do RabbitMQ ji pošle listener AŽ PO commitu.
        events.publishEvent(new TransferCompletedEvent(idempotencyKey, fromId, toId, amount));

        log.info("Transfer OK: {} from account {} to account {} (key {})", amount, fromId, toId, idempotencyKey);
        return new TransferResponse(fromId, toId, amount, from.getBalance(), to.getBalance());
    }
}