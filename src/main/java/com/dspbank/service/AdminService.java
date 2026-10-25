package com.dspbank.service;

import static org.springframework.http.HttpStatus.*;

import com.dspbank.dto.Dto.*;
import com.dspbank.entity.Account;
import com.dspbank.exception.ApiException;
import com.dspbank.repository.*;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository admins;
    private final AccountRepository accounts;
    private final TransactionRepository txs;
    private final PasswordEncoder encoder;
    private final AuthService auth;

    public AuthRes login(AdminLoginReq r) {
        var admin = admins.findByUsername(r.username())
                .filter(a -> encoder.matches(r.password(), a.getPasswordHash()))
                .orElseThrow(() -> new ApiException(UNAUTHORIZED, "Invalid admin username or password."));
        return new AuthRes(auth.open("ADMIN", null), "ADMIN", null, admin.getUsername());
    }

    public List<AccountView> all() {
        return accounts.findAll().stream().map(BankService::view).toList();
    }

    public AccountView find(Long no) { return BankService.view(byNumber(no)); }

    @Transactional
    public AccountView update(Long no, UpdateAccountReq r) {
        Account a = byNumber(no);
        a.setHolderName(r.name().trim());
        if (r.pin() != null && !r.pin().isBlank()) a.setPinHash(encoder.encode(r.pin()));
        return BankService.view(a);
    }

    @Transactional
    public void delete(Long no) {
        byNumber(no);
        txs.deleteByAccountNumber(no);
        accounts.deleteById(no);
    }

    @Transactional
    public AccountView setStatus(Long no, String status) {
        Account a = byNumber(no);
        a.setStatus(status);
        return BankService.view(a);
    }

    @Transactional
    public void resetPin(Long no, String newPin) { byNumber(no).setPinHash(encoder.encode(newPin)); }

    public Stats stats() {
        return new Stats(accounts.count(), accounts.countByStatus("BLOCKED"), accounts.totalBalance(), txs.count());
    }

    private Account byNumber(Long no) {
        return accounts.findById(no).orElseThrow(() -> new ApiException(NOT_FOUND, "Account not found."));
    }
}
