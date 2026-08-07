package com.example.payments.messaging;

import com.example.payments.common.CorrelationId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TransferEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(TransferEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public TransferEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTransferCompleted(TransferCompletedEvent event) {
        String correlationId = MDC.get(CorrelationId.MDC_KEY);
        log.info("Transaction committed -> sending event to RabbitMQ: {}", event);
        rabbitTemplate.convertAndSend(RabbitConfig.TRANSFER_COMPLETED_QUEUE, event, message -> {
            message.getMessageProperties().setHeader(CorrelationId.HEADER, correlationId);
            return message;
        });
    }
}