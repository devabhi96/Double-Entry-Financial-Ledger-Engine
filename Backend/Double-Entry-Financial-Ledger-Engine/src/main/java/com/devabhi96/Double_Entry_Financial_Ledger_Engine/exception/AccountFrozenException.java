package com.devabhi96.Double_Entry_Financial_Ledger_Engine.exception;

import java.util.UUID;

public class AccountFrozenException extends RuntimeException {
    public AccountFrozenException(UUID id) { super("Account " + id + " is frozen."); }
}