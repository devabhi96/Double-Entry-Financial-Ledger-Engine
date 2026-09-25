package com.devabhi96.Double_Entry_Financial_Ledger_Engine.exception;

public class IdempotencyConflictException extends RuntimeException {
    public IdempotencyConflictException(String message) { super(message); }
}