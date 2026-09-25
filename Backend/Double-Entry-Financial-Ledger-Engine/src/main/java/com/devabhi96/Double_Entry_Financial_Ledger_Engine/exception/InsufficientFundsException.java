package com.devabhi96.Double_Entry_Financial_Ledger_Engine.exception;

import java.math.BigDecimal;
import java.util.UUID;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(UUID accountId) {
        super("Account " + accountId + " does not have sufficient balance for this transfer.");
    }
}