package com.dspbank.controller;

import com.dspbank.dto.Dto.*;
import com.dspbank.service.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BankController {
    private static final String TOKEN = "X-Auth-Token";
    private final BankService bank;
    private final AdminService admin;
    private final AuthService auth;

    // ---- public ----
    @PostMapping("/auth/register") @ResponseStatus(HttpStatus.CREATED)
    public AccountView register(@Valid @RequestBody RegisterReq r) { return bank.register(r); }

    @PostMapping("/auth/login")
    public AuthRes login(@Valid @RequestBody LoginReq r) { return bank.login(r); }

    @PostMapping("/auth/admin/login")
    public AuthRes adminLogin(@Valid @RequestBody AdminLoginReq r) { return admin.login(r); }

    @PostMapping("/auth/logout")
    public Map<String, String> logout(@RequestHeader(value = TOKEN, required = false) String t) {
        auth.close(t);
        return Map.of("message", "Logged out.");
    }

    // ---- customer (needs a CUSTOMER token) ----
    @GetMapping("/account/me")
    public AccountView me(@RequestHeader(value = TOKEN, required = false) String t) {
        return bank.get(no(t));
    }

    @PostMapping("/account/deposit")
    public AccountView deposit(@RequestHeader(value = TOKEN, required = false) String t, @Valid @RequestBody AmountReq r) {
        return bank.deposit(no(t), r.amount());
    }

    @PostMapping("/account/withdraw")
    public AccountView withdraw(@RequestHeader(value = TOKEN, required = false) String t, @Valid @RequestBody AmountReq r) {
        return bank.withdraw(no(t), r.amount());
    }

    @PostMapping("/account/transfer")
    public AccountView transfer(@RequestHeader(value = TOKEN, required = false) String t, @Valid @RequestBody TransferReq r) {
        return bank.transfer(no(t), r.toAccount(), r.amount());
    }

    @GetMapping("/account/transactions")
    public List<TxView> history(@RequestHeader(value = TOKEN, required = false) String t,
                                @RequestParam(defaultValue = "50") int limit) {
        return bank.history(no(t), limit);
    }

    @PutMapping("/account/pin")
    public Map<String, String> changePin(@RequestHeader(value = TOKEN, required = false) String t, @Valid @RequestBody ChangePinReq r) {
        bank.changePin(no(t), r.oldPin(), r.newPin());
        return Map.of("message", "PIN changed.");
    }

    private Long no(String token) { return auth.require(token, "CUSTOMER").accountNumber(); }
}
