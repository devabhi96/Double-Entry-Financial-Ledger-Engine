package com.devabhi96.Double_Entry_Financial_Ledger_Engine.exception;

import java.util.UUID;

public class TransferNotFoundException extends RuntimeException {
    public TransferNotFoundException(UUID txnId) { super("Transaction " + txnId + " not found."); }
}