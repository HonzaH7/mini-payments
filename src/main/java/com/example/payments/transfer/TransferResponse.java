package com.example.payments.transfer;

import java.math.BigDecimal;

public record TransferResponse(
            Long from, 
            Long to, 
            BigDecimal amount, 
            BigDecimal fromBalance, 
            BigDecimal toBalance) {}