package com.dspbank.service;

import static org.springframework.http.HttpStatus.*;

import com.dspbank.dto.Dto.*;
import com.dspbank.entity.*;
import com.dspbank.exception.ApiException;
import com.dspbank.repository.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BankService {
    private final AccountRepository accounts;
    private final TransactionRepository txs;
    private final PasswordEncoder encoder;
    private final AuthService auth;
    private final SecureRandom rnd = new SecureRandom();

    public static AccountView view(Account a) {
        return new AccountView(a.getAccountNumber(), a.getHolderName(), a.getBalance(), a.getStatus(), a.getCreatedAt());
    }
    private static BigDecimal money(BigDecimal v) { return v.setScale(2, RoundingMode.HALF_UP); }

    // ---------- account lifecycle ----------
    @Transactional
    public AccountView register(RegisterReq r) {
        long no;
        do { no = rnd.nextLong(1_000_000_000L, 9_999_999_999L); } while (accounts.existsById(no));
        Account a = new Account();
        a.setAccountNumber(no);
        a.setHolderName(r.name().trim());
        a.setPinHash(encoder.encode(r.pin()));
        a.setBalance(money(r.initialDeposit()));
        accounts.save(a);
        if (a.getBalance().signum() > 0) record(a, "DEPOSIT", a.getBalance(), null);
        return view(a);
    }

    public AuthRes login(LoginReq r) {
        Account a = accounts.findById(r.accountNumber())
                .filter(x -> encoder.matches(r.pin(), x.getPinHash()))
                .orElseThrow(() -> new ApiException(UNAUTHORIZED, "Invalid account number or PIN."));
        if ("BLOCKED".equalsIgnoreCase(a.getStatus()))
            throw new ApiException(FORBIDDEN, "Account is blocked. Contact the administrator.");
        return new AuthRes(auth.open("CUSTOMER", a.getAccountNumber()), "CUSTOMER", a.getAccountNumber(), a.getHolderName());
    }

    public AccountView get(Long no) {
        return view(accounts.findById(no).orElseThrow(() -> new ApiException(NOT_FOUND, "Account not found.")));
    }

    // ---------- money movement ----------
    @Transactional
    public AccountView deposit(Long no, BigDecimal amount) {
        Account a = lockActive(no);
        a.setBalance(a.getBalance().add(money(amount)));
        record(a, "DEPOSIT", money(amount), null);
        return view(a);
    }

    @Transactional
    public AccountView withdraw(Long no, BigDecimal amount) {
        Account a = lockActive(no);
        BigDecimal amt = money(amount);
        if (a.getBalance().compareTo(amt) < 0) throw new ApiException(BAD_REQUEST, "Insufficient balance.");
        a.setBalance(a.getBalance().subtract(amt));
        record(a, "WITHDRAW", amt, null);
        return view(a);
    }

    @Transactional
    public AccountView transfer(Long from, Long to, BigDecimal amount) {
        if (from.equals(to)) throw new ApiException(BAD_REQUEST, "You cannot transfer to your own account.");
        BigDecimal amt = money(amount);
        // Always lock the lower account number first so two opposite transfers can't deadlock.
        Account first = lock(Math.min(from, to)), second = lock(Math.max(from, to));
        Account sender = first.getAccountNumber().equals(from) ? first : second;
        Account receiver = sender == first ? second : first;
        requireActive(sender);
        if ("BLOCKED".equalsIgnoreCase(receiver.getStatus()))
            throw new ApiException(BAD_REQUEST, "Receiver account is blocked.");
        if (sender.getBalance().compareTo(amt) < 0) throw new ApiException(BAD_REQUEST, "Insufficient balance.");
        sender.setBalance(sender.getBalance().subtract(amt));
        receiver.setBalance(receiver.getBalance().add(amt));
        record(sender, "TRANSFER_OUT", amt, to);
        record(receiver, "TRANSFER_IN", amt, from);
        return view(sender);
    }

    public List<TxView> history(Long no, int limit) {
        int size = Math.max(1, Math.min(limit, 200));
        return txs.findByAccountNumberOrderByCreatedAtDescIdDesc(no, PageRequest.of(0, size)).stream()
                .map(t -> new TxView(t.getId(), t.getType(), t.getAmount(), t.getBalanceAfter(), t.getCounterparty(), t.getCreatedAt()))
                .toList();
    }

    @Transactional
    public void changePin(Long no, String oldPin, String newPin) {
        Account a = lockActive(no);
        if (!encoder.matches(oldPin, a.getPinHash())) throw new ApiException(BAD_REQUEST, "Old PIN is incorrect.");
        a.setPinHash(encoder.encode(newPin));
    }

    // ---------- helpers ----------
    private Account lock(Long no) {
        return accounts.lockByNumber(no).orElseThrow(() -> new ApiException(NOT_FOUND, "Account " + no + " not found."));
    }
    private Account lockActive(Long no) { Account a = lock(no); requireActive(a); return a; }
    private void requireActive(Account a) {
        if ("BLOCKED".equalsIgnoreCase(a.getStatus()))
            throw new ApiException(FORBIDDEN, "Account is blocked. Contact the administrator.");
    }
    private void record(Account a, String type, BigDecimal amount, Long counterparty) {
        BankTransaction t = new BankTransaction();
        t.setAccountNumber(a.getAccountNumber());
        t.setType(type);
        t.setAmount(amount);
        t.setBalanceAfter(a.getBalance());
        t.setCounterparty(counterparty);
        txs.save(t);
    }
}
