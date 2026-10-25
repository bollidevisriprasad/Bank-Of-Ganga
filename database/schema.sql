-- MySQL schema for DSP Bank. Spring Boot creates these automatically (ddl-auto=update);
-- this file is for reference, or if you prefer to create the tables by hand.
CREATE DATABASE IF NOT EXISTS dspbank;
USE dspbank;

CREATE TABLE IF NOT EXISTS accounts (
  account_number      BIGINT        NOT NULL PRIMARY KEY,
  account_holder_name VARCHAR(100)  NOT NULL,
  pin_hash            VARCHAR(255)  NOT NULL,                    -- BCrypt hash, never the raw PIN
  balance             DECIMAL(15,2) NOT NULL DEFAULT 0.00,
  status              VARCHAR(10)   NOT NULL DEFAULT 'ACTIVE',   -- ACTIVE | BLOCKED
  created_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS transactions (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  account_number   BIGINT        NOT NULL,
  transaction_type VARCHAR(20)   NOT NULL,   -- DEPOSIT | WITHDRAW | TRANSFER_OUT | TRANSFER_IN
  amount           DECIMAL(15,2) NOT NULL,
  balance_after    DECIMAL(15,2),
  counterparty     BIGINT,                   -- other account in a transfer
  transaction_date DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_tx_account (account_number),
  CONSTRAINT fk_tx_account FOREIGN KEY (account_number) REFERENCES accounts(account_number) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS admins (
  admin_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
  username      VARCHAR(50)  NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL                          -- BCrypt hash
);
