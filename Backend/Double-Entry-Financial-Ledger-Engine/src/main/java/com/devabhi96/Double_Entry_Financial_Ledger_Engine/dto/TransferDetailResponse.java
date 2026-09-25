package com.devabhi96.Double_Entry_Financial_Ledger_Engine.dto;

import com.devabhi96.Double_Entry_Financial_Ledger_Engine.entity.LedgerEntry;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TransferDetailResponse(UUID txnId, List<EntryDto> entries, Instant createdAt) {

    public record EntryDto(UUID accountId, String type, String amount, String balanceAfter) {}

    public static TransferDetailResponse from(UUID txnId, List<LedgerEntry> entries) {
        List<EntryDto> dtos = entries.stream()
                .map(e -> new EntryDto(
                        e.getAccountId(),
                        e.getType().name(),
                        e.getAmount().setScale(2, java.math.RoundingMode.HALF_EVEN).toPlainString(),
                        e.getBalanceAfter().setScale(2, java.math.RoundingMode.HALF_EVEN).toPlainString()))
                .toList();
        Instant createdAt = entries.isEmpty() ? null : entries.get(0).getCreatedAt();
        return new TransferDetailResponse(txnId, dtos, createdAt);
    }
}