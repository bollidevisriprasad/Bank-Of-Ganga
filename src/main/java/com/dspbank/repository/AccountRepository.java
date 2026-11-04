
package com.dspbank.repository;

import com.dspbank.entity.Account;
import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Row lock so two requests cannot change
     * the same account balance simultaneously.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.accountNumber = :n")
    Optional<Account> lockByNumber(@Param("n") Long n);

    @Query("select coalesce(sum(a.balance), 0) from Account a")
    BigDecimal totalBalance();

    long countByStatus(String status);
}

