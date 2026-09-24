package com.devabhi96.Double_Entry_Financial_Ledger_Engine.exception;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException{
    public AccountNotFoundException(UUID id){
        super("Account " + id + " not found.");
    }
}
