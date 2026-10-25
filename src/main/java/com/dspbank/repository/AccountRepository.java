package com.dspbank.repository;

import com.dspbank.entity.Account;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, Long> {

    /** Row lock so two requests can never change the same balance at once. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.accountNumber = :n")
    Optional<Account> lockByNumber(@Param("n") Long n);

    @Query("select coalesce(sum(a.balance), 0) from Account a")
    BigDecimal totalBalance();

    long countByStatus(String status);
}
