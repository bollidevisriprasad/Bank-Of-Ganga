package com.dspbank.repository;

import com.dspbank.entity.BankTransaction;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<BankTransaction, Long> {
    List<BankTransaction> findByAccountNumberOrderByCreatedAtDescIdDesc(Long accountNumber, Pageable page);
    void deleteByAccountNumber(Long accountNumber);
}
