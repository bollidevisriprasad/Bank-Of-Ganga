package com.dspbank.config;

import com.dspbank.entity.*;
import com.dspbank.repository.*;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    /** First-run data so you can log in immediately. Change or remove before any real deployment. */
    @Bean
    CommandLineRunner seed(AdminRepository admins, AccountRepository accounts,
                           TransactionRepository txs, PasswordEncoder enc) {
        return args -> {
            if (admins.count() == 0) {
                Admin a = new Admin();
                a.setUsername("admin");
                a.setPasswordHash(enc.encode("admin123"));
                admins.save(a);
            }
            if (accounts.count() == 0) {
                demo(accounts, txs, enc, 1234567890L, "Ravi", "1234", "5000.00");
                demo(accounts, txs, enc, 123456781L, "DSP", "4321", "10000.00");
            }
        };
    }

    private void demo(AccountRepository accounts, TransactionRepository txs, PasswordEncoder enc,
                      long no, String name, String pin, String balance) {
        Account a = new Account();
        a.setAccountNumber(no);
        a.setHolderName(name);
        a.setPinHash(enc.encode(pin));
        a.setBalance(new BigDecimal(balance));
        accounts.save(a);
        BankTransaction t = new BankTransaction();
        t.setAccountNumber(no);
        t.setType("DEPOSIT");
        t.setAmount(a.getBalance());
        t.setBalanceAfter(a.getBalance());
        txs.save(t);
    }
}
