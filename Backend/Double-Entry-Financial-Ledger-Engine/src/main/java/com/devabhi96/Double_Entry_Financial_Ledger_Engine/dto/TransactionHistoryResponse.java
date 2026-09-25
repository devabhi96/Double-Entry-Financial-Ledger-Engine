package com.devabhi96.Double_Entry_Financial_Ledger_Engine.dto;

import com.devabhi96.Double_Entry_Financial_Ledger_Engine.entity.LedgerEntry;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TransactionHistoryResponse(int page, int size, long totalItems, List<Item> items) {

    public record Item(UUID txnId, String type, String amount, Instant createdAt) {}

    public static TransactionHistoryResponse from(Page<LedgerEntry> p) {
        List<Item> items = p.getContent().stream()
                .map(e -> new Item(e.getTxnId(), e.getType().name(),
                        e.getAmount().setScale(2, java.math.RoundingMode.HALF_EVEN).toPlainString(),
                        e.getCreatedAt()))
                .toList();
        return new TransactionHistoryResponse(p.getNumber() + 1, p.getSize(), p.getTotalElements(), items);
    }
}