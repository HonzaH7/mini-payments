package com.example.payments.messaging;

import java.math.BigDecimal;

/** Událost: převod se úspěšně dokončil. Nosič dat, který poputuje do RabbitMQ. */
public record TransferCompletedEvent(
        String idempotencyKey,
        Long fromAccount,
        Long toAccount,
        BigDecimal amount) {}