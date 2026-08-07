package com.example.payments.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String TRANSFER_COMPLETED_QUEUE = "transfer.completed";
    public static final String DEAD_LETTER_EXCHANGE = "transfer.dlx";
    public static final String TRANSFER_COMPLETED_DLQ = "transfer.completed.dlq";

    // Hlavní fronta. x-dead-letter-exchange = kam poslat odmítnuté zprávy.
    @Bean
    public Queue transferCompletedQueue() {
        return QueueBuilder.durable(TRANSFER_COMPLETED_QUEUE)
                .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE)
                .build();
    }

    // Dead letter exchange (fanout = doručí všem navázaným frontám).
    @Bean
    public FanoutExchange deadLetterExchange() {
        return new FanoutExchange(DEAD_LETTER_EXCHANGE);
    }

    // Dead letter queue - sem padají zprávy, které se nepovedlo zpracovat.
    @Bean
    public Queue transferCompletedDlq() {
        return QueueBuilder.durable(TRANSFER_COMPLETED_DLQ).build();
    }

    // Napojení DLQ na DLX.
    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(transferCompletedDlq()).to(deadLetterExchange());
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}