

package com.dspbank.dto;

import javax.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class Dto {

    private Dto() {}

    static final String PIN_MSG = "PIN must be exactly 4 digits.";

    // ==========================================
    // REQUEST DTOs
    // ==========================================

    public static class RegisterReq {

        @NotBlank(message = "Name is required.")
        @Size(max = 100)
        private final String name;

        @NotBlank(message = "PIN is required.")
        @Pattern(regexp = "\\d{4}", message = PIN_MSG)
        private final String pin;

        @NotNull(message = "Initial deposit is required.")
        @DecimalMin(value = "0.00", message = "Initial deposit cannot be negative.")
        private final BigDecimal initialDeposit;

        @JsonCreator
        public RegisterReq(
                @JsonProperty("name") String name,
                @JsonProperty("pin") String pin,
                @JsonProperty("initialDeposit") BigDecimal initialDeposit) {
            this.name = name;
            this.pin = pin;
            this.initialDeposit = initialDeposit;
        }

        public String name() { return name; }
        public String pin() { return pin; }
        public BigDecimal initialDeposit() { return initialDeposit; }
    }

    public static class LoginReq {

        @NotNull(message = "Account number is required.")
        private final Long accountNumber;

        @NotBlank(message = "PIN is required.")
        private final String pin;

        @JsonCreator
        public LoginReq(
                @JsonProperty("accountNumber") Long accountNumber,
                @JsonProperty("pin") String pin) {
            this.accountNumber = accountNumber;
            this.pin = pin;
        }

        public Long accountNumber() { return accountNumber; }
        public String pin() { return pin; }
    }

    public static class AdminLoginReq {

        @NotBlank(message = "Username is required.")
        private final String username;

        @NotBlank(message = "Password is required.")
        private final String password;

        @JsonCreator
        public AdminLoginReq(
                @JsonProperty("username") String username,
                @JsonProperty("password") String password) {
            this.username = username;
            this.password = password;
        }

        public String username() { return username; }
        public String password() { return password; }
    }

    public static class AmountReq {

        @NotNull(message = "Amount is required.")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0.")
        private final BigDecimal amount;

        @JsonCreator
        public AmountReq(@JsonProperty("amount") BigDecimal amount) {
            this.amount = amount;
        }

        public BigDecimal amount() { return amount; }
    }

    public static class TransferReq {

        @NotNull(message = "Receiver account is required.")
        private final Long toAccount;

        @NotNull(message = "Amount is required.")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0.")
        private final BigDecimal amount;

        @JsonCreator
        public TransferReq(
                @JsonProperty("toAccount") Long toAccount,
                @JsonProperty("amount") BigDecimal amount) {
            this.toAccount = toAccount;
            this.amount = amount;
        }

        public Long toAccount() { return toAccount; }
        public BigDecimal amount() { return amount; }
    }

    public static class ChangePinReq {

        @NotBlank(message = "Old PIN is required.")
        private final String oldPin;

        @NotBlank(message = "New PIN is required.")
        @Pattern(regexp = "\\d{4}", message = PIN_MSG)
        private final String newPin;

        @JsonCreator
        public ChangePinReq(
                @JsonProperty("oldPin") String oldPin,
                @JsonProperty("newPin") String newPin) {
            this.oldPin = oldPin;
            this.newPin = newPin;
        }

        public String oldPin() { return oldPin; }
        public String newPin() { return newPin; }
    }

    public static class ResetPinReq {

        @NotBlank(message = "New PIN is required.")
        @Pattern(regexp = "\\d{4}", message = PIN_MSG)
        private final String newPin;

        @JsonCreator
        public ResetPinReq(@JsonProperty("newPin") String newPin) {
            this.newPin = newPin;
        }

        public String newPin() { return newPin; }
    }

    public static class UpdateAccountReq {

        @NotBlank(message = "Name is required.")
        @Size(max = 100)
        private final String name;

        @Pattern(regexp = "\\d{4}", message = PIN_MSG)
        private final String pin;

        @JsonCreator
        public UpdateAccountReq(
                @JsonProperty("name") String name,
                @JsonProperty("pin") String pin) {
            this.name = name;
            this.pin = pin;
        }

        public String name() { return name; }
        public String pin() { return pin; }
    }

    // ==========================================
    // RESPONSE DTOs
    // ==========================================

    public static class AuthRes {

        private final String token;
        private final String role;
        private final Long accountNumber;
        private final String name;

        @JsonCreator
        public AuthRes(
                @JsonProperty("token") String token,
                @JsonProperty("role") String role,
                @JsonProperty("accountNumber") Long accountNumber,
                @JsonProperty("name") String name) {
            this.token = token;
            this.role = role;
            this.accountNumber = accountNumber;
            this.name = name;
        }

        // JavaBean getters for Jackson
        public String getToken() { return token; }
        public String getRole() { return role; }
        public Long getAccountNumber() { return accountNumber; }
        public String getName() { return name; }

        // Existing accessors
        public String token() { return token; }
        public String role() { return role; }
        public Long accountNumber() { return accountNumber; }
        public String name() { return name; }
    }

    public static class AccountView {

        private final Long accountNumber;
        private final String name;
        private final BigDecimal balance;
        private final String status;
        private final LocalDateTime createdAt;

        @JsonCreator
        public AccountView(
                @JsonProperty("accountNumber") Long accountNumber,
                @JsonProperty("name") String name,
                @JsonProperty("balance") BigDecimal balance,
                @JsonProperty("status") String status,
                @JsonProperty("createdAt") LocalDateTime createdAt) {
            this.accountNumber = accountNumber;
            this.name = name;
            this.balance = balance;
            this.status = status;
            this.createdAt = createdAt;
        }

        public Long getAccountNumber() { return accountNumber; }
        public String getName() { return name; }
        public BigDecimal getBalance() { return balance; }
        public String getStatus() { return status; }
        public LocalDateTime getCreatedAt() { return createdAt; }

        public Long accountNumber() { return accountNumber; }
        public String name() { return name; }
        public BigDecimal balance() { return balance; }
        public String status() { return status; }
        public LocalDateTime createdAt() { return createdAt; }
    }

    public static class TxView {

        private final Long id;
        private final String type;
        private final BigDecimal amount;
        private final BigDecimal balanceAfter;
        private final Long counterparty;
        private final LocalDateTime date;

        @JsonCreator
        public TxView(
                @JsonProperty("id") Long id,
                @JsonProperty("type") String type,
                @JsonProperty("amount") BigDecimal amount,
                @JsonProperty("balanceAfter") BigDecimal balanceAfter,
                @JsonProperty("counterparty") Long counterparty,
                @JsonProperty("date") LocalDateTime date) {
            this.id = id;
            this.type = type;
            this.amount = amount;
            this.balanceAfter = balanceAfter;
            this.counterparty = counterparty;
            this.date = date;
        }

        public Long getId() { return id; }
        public String getType() { return type; }
        public BigDecimal getAmount() { return amount; }
        public BigDecimal getBalanceAfter() { return balanceAfter; }
        public Long getCounterparty() { return counterparty; }
        public LocalDateTime getDate() { return date; }

        public Long id() { return id; }
        public String type() { return type; }
        public BigDecimal amount() { return amount; }
        public BigDecimal balanceAfter() { return balanceAfter; }
        public Long counterparty() { return counterparty; }
        public LocalDateTime date() { return date; }
    }

    public static class Stats {

        private final long totalCustomers;
        private final long blockedAccounts;
        private final BigDecimal totalBalance;
        private final long totalTransactions;

        @JsonCreator
        public Stats(
                @JsonProperty("totalCustomers") long totalCustomers,
                @JsonProperty("blockedAccounts") long blockedAccounts,
                @JsonProperty("totalBalance") BigDecimal totalBalance,
                @JsonProperty("totalTransactions") long totalTransactions) {
            this.totalCustomers = totalCustomers;
            this.blockedAccounts = blockedAccounts;
            this.totalBalance = totalBalance;
            this.totalTransactions = totalTransactions;
        }

        public long getTotalCustomers() { return totalCustomers; }
        public long getBlockedAccounts() { return blockedAccounts; }
        public BigDecimal getTotalBalance() { return totalBalance; }
        public long getTotalTransactions() { return totalTransactions; }

        public long totalCustomers() { return totalCustomers; }
        public long blockedAccounts() { return blockedAccounts; }
        public BigDecimal totalBalance() { return totalBalance; }
        public long totalTransactions() { return totalTransactions; }
    }
}
