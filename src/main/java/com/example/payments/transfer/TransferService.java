package com.example.payments.transfer;

import com.example.payments.account.Account;
import com.example.payments.account.AccountRepository;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferService {
    private static final Logger log = LoggerFactory.getLogger(TransferService.class);
    private final AccountRepository accounts;
    
    public TransferService(AccountRepository accounts) {
        this.accounts = accounts;
    }
    
    @Transactional
    public TransferResponse transfer(Long fromId, Long toId, BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");        
        }
        
        Account from = accounts.findById(fromId)
                .orElseThrow( () -> new IllegalArgumentException("Account does not exist: " + fromId));
        Account to = accounts.findById(toId)
                .orElseThrow( () -> new IllegalArgumentException("Account does not exist: " + toId));
        
        from.setBalance(from.getBalance().subtract(amount));
        log.info("Debited {} from account {} -> balance IN MEMORY {}", amount, fromId, from.getBalance());
        
        to.setBalance(to.getBalance().add(amount));
        log.info("Credited {} to account {} -> balance IN MEMORY {}", amount, toId, to.getBalance());
        
        if (from.getBalance().signum() < 0) {
            throw new InsufficientFundsException(
                "Insufficient funds in account " + fromId + " for transfer " + amount);
        }
        
        log.info("Transfer OK: {} from account {} to account {}", amount, fromId, toId);
        return new TransferResponse(fromId, toId, amount, from.getBalance(), to.getBalance());
    }
}