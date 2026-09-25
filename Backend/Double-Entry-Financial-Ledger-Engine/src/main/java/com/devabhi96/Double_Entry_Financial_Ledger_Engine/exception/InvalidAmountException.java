package com.devabhi96.Double_Entry_Financial_Ledger_Engine.exception;

public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException() { super("Transfer amount must be positive."); }
}