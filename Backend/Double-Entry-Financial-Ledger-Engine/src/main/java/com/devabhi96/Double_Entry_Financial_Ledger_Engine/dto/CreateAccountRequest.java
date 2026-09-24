package com.devabhi96.Double_Entry_Financial_Ledger_Engine.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateAccountRequest (
        @NotNull @Positive Long userId
){}

