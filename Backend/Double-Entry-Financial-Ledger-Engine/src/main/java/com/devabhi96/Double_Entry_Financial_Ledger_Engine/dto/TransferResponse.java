package com.devabhi96.Double_Entry_Financial_Ledger_Engine.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferResponse(
        UUID txnId,
        String status,
        UUID fromAccountId,
        UUID toAccountId,
        String amount,
        Instant createdAt
) {
    public static TransferResponse of(UUID txnId, UUID from, UUID to, BigDecimal amount, Instant createdAt) {
        return new TransferResponse(txnId, "COMPLETED", from, to,
                amount.setScale(2, java.math.RoundingMode.HALF_EVEN).toPlainString(), createdAt);
    }
}