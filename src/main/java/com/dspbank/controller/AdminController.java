package com.dspbank.controller;

import com.dspbank.dto.Dto.*;
import com.dspbank.service.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private static final String TOKEN = "X-Auth-Token";
    private final AdminService admin;
    private final AuthService auth;

    @GetMapping("/stats")
    public Stats stats(@RequestHeader(value = TOKEN, required = false) String t) { guard(t); return admin.stats(); }

    @GetMapping("/accounts")
    public List<AccountView> all(@RequestHeader(value = TOKEN, required = false) String t) { guard(t); return admin.all(); }

    @GetMapping("/accounts/{no}")
    public AccountView one(@RequestHeader(value = TOKEN, required = false) String t, @PathVariable Long no) {
        guard(t); return admin.find(no);
    }

    @PutMapping("/accounts/{no}")
    public AccountView update(@RequestHeader(value = TOKEN, required = false) String t, @PathVariable Long no,
                              @Valid @RequestBody UpdateAccountReq r) {
        guard(t); return admin.update(no, r);
    }

    @DeleteMapping("/accounts/{no}")
    public Map<String, String> delete(@RequestHeader(value = TOKEN, required = false) String t, @PathVariable Long no) {
        guard(t); admin.delete(no); return Map.of("message", "Account deleted.");
    }

    @PostMapping("/accounts/{no}/block")
    public AccountView block(@RequestHeader(value = TOKEN, required = false) String t, @PathVariable Long no) {
        guard(t); return admin.setStatus(no, "BLOCKED");
    }

    @PostMapping("/accounts/{no}/unblock")
    public AccountView unblock(@RequestHeader(value = TOKEN, required = false) String t, @PathVariable Long no) {
        guard(t); return admin.setStatus(no, "ACTIVE");
    }

    @PostMapping("/accounts/{no}/reset-pin")
    public Map<String, String> resetPin(@RequestHeader(value = TOKEN, required = false) String t, @PathVariable Long no,
                                        @Valid @RequestBody ResetPinReq r) {
        guard(t); admin.resetPin(no, r.newPin()); return Map.of("message", "PIN reset.");
    }

    private void guard(String token) { auth.require(token, "ADMIN"); }
}
