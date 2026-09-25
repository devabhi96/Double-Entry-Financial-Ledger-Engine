package com.devabhi96.Double_Entry_Financial_Ledger_Engine.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(
        @NotNull UUID fromAccountId,
        @NotNull UUID toAccountId,
        @NotNull BigDecimal amount,
        String note
) {}