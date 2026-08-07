package com.example.payments.messaging;

/** Odeslání potvrzení platby selhalo (spustí retry a případně DLQ). */
public class ConfirmationFailedException extends RuntimeException {

    public ConfirmationFailedException(String message) {
        super(message);
    }
}