package com.example.payments.messaging;

import com.example.payments.common.CorrelationId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class TransferCompletedConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransferCompletedConsumer.class);

    @RabbitListener(queues = RabbitConfig.TRANSFER_COMPLETED_QUEUE)
    public void onMessage(
            TransferCompletedEvent event,
            @Header(name = CorrelationId.HEADER, required = false) String correlationId) {
        if (correlationId != null) {
            MDC.put(CorrelationId.MDC_KEY, correlationId);
        }
        try {
            log.info("Consumer received event: {}", event);
            if (event.idempotencyKey().startsWith("fail")) {
                throw new RuntimeException("Simulated consumer failure for key " + event.idempotencyKey());
            }
            log.info("Payment confirmation sent for key {}", event.idempotencyKey());
        } finally {
            MDC.remove(CorrelationId.MDC_KEY);
        }
    }
}