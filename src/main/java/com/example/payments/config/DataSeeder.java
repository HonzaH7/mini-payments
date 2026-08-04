package com.example.payments.config;

import com.example.payments.account.Account;
import com.example.payments.account.AccountRepository;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Naplní dva testovací účty při startu - ale jen když je tabulka prázdná,
 * takže opakovaný restart nevytvoří duplicity.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final AccountRepository accounts;

    public DataSeeder(AccountRepository accounts) {
        this.accounts = accounts;
    }

    @Override
    public void run(String... args) {
        if (accounts.count() > 0) {
            log.info("Účty už existují ({}), seed přeskočen.", accounts.count());
            return;
        }
        accounts.save(new Account("alice", new BigDecimal("1000.00")));
        accounts.save(new Account("bob", new BigDecimal("500.00")));
        log.info("Seed hotov: vytvořeny 2 účty (alice=1000.00, bob=500.00).");
    }
}
