package com.example.payments.transfer;

import java.math.BigDecimal;

public record TransferRequest(Long from, Long to, BigDecimal amount) {
    
}