package com.dspbank.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class Dto {
    private Dto() {}
    static final String PIN_MSG = "PIN must be exactly 4 digits.";

    // ---- requests ----
    public record RegisterReq(
            @NotBlank(message = "Name is required.") @Size(max = 100) String name,
            @NotBlank(message = "PIN is required.") @Pattern(regexp = "\\d{4}", message = PIN_MSG) String pin,
            @NotNull(message = "Initial deposit is required.") @DecimalMin(value = "0.00", message = "Initial deposit cannot be negative.") BigDecimal initialDeposit) {}
    public record LoginReq(@NotNull(message = "Account number is required.") Long accountNumber,
                           @NotBlank(message = "PIN is required.") String pin) {}
    public record AdminLoginReq(@NotBlank(message = "Username is required.") String username,
                                @NotBlank(message = "Password is required.") String password) {}
    public record AmountReq(@NotNull(message = "Amount is required.") @DecimalMin(value = "0.01", message = "Amount must be greater than 0.") BigDecimal amount) {}
    public record TransferReq(@NotNull(message = "Receiver account is required.") Long toAccount,
                              @NotNull(message = "Amount is required.") @DecimalMin(value = "0.01", message = "Amount must be greater than 0.") BigDecimal amount) {}
    public record ChangePinReq(@NotBlank(message = "Old PIN is required.") String oldPin,
                               @NotBlank(message = "New PIN is required.") @Pattern(regexp = "\\d{4}", message = PIN_MSG) String newPin) {}
    public record ResetPinReq(@NotBlank(message = "New PIN is required.") @Pattern(regexp = "\\d{4}", message = PIN_MSG) String newPin) {}
    public record UpdateAccountReq(@NotBlank(message = "Name is required.") @Size(max = 100) String name,
                                   @Pattern(regexp = "\\d{4}", message = PIN_MSG) String pin) {}   // pin optional

    // ---- responses ----
    public record AuthRes(String token, String role, Long accountNumber, String name) {}
    public record AccountView(Long accountNumber, String name, BigDecimal balance, String status, LocalDateTime createdAt) {}
    public record TxView(Long id, String type, BigDecimal amount, BigDecimal balanceAfter, Long counterparty, LocalDateTime date) {}
    public record Stats(long totalCustomers, long blockedAccounts, BigDecimal totalBalance, long totalTransactions) {}
}
