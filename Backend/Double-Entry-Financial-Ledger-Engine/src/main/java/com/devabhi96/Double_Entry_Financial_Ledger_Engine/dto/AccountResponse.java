package com.devabhi96.Double_Entry_Financial_Ledger_Engine.dto;

import com.devabhi96.Double_Entry_Financial_Ledger_Engine.entity.Account;

import java.util.UUID;

public record AccountResponse(UUID id, Long userId , String balance , String status) {

        public static AccountResponse from(Account a){
            String balance = a.getBalance().setScale(2, java.math.RoundingMode.HALF_EVEN).toPlainString();
            return new AccountResponse(a.getId(), a.getUserId(), balance, a.getStatus().name());
        }
}
