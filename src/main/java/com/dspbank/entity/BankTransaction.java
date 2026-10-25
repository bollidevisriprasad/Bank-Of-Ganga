package com.dspbank.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transactions", indexes = @Index(name = "idx_tx_account", columnList = "account_number"))
@Getter @Setter @NoArgsConstructor
public class BankTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false)
    private Long accountNumber;

    @Column(name = "transaction_type", nullable = false, length = 20)
    private String type;   // DEPOSIT, WITHDRAW, TRANSFER_OUT, TRANSFER_IN

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_after", precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    private Long counterparty;   // other account in a transfer

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
