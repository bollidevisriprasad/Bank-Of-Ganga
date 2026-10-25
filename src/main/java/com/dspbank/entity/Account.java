package com.dspbank.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "accounts")
@Getter @Setter @NoArgsConstructor
public class Account {
    @Id
    @Column(name = "account_number")
    private Long accountNumber;

    @Column(name = "account_holder_name", nullable = false, length = 100)
    private String holderName;

    @Column(name = "pin_hash", nullable = false)
    private String pinHash;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false, length = 10)
    private String status = "ACTIVE";   // ACTIVE or BLOCKED

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
