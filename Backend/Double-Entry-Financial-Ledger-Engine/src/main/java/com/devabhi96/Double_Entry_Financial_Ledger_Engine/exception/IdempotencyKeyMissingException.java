package com.devabhi96.Double_Entry_Financial_Ledger_Engine.exception;

public class IdempotencyKeyMissingException extends RuntimeException {
    public IdempotencyKeyMissingException() { super("Idempotency-Key header is required."); }
}