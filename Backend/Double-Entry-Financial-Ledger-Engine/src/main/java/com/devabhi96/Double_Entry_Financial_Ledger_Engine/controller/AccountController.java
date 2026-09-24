package com.devabhi96.Double_Entry_Financial_Ledger_Engine.controller;

import com.devabhi96.Double_Entry_Financial_Ledger_Engine.dto.AccountResponse;
import com.devabhi96.Double_Entry_Financial_Ledger_Engine.dto.CreateAccountRequest;
import com.devabhi96.Double_Entry_Financial_Ledger_Engine.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest request) {
        return AccountResponse.from(accountService.createAccount(request.userId()));
    }

    @GetMapping("/{accountId}")
    public AccountResponse get(@PathVariable UUID accountId) {
        return AccountResponse.from(accountService.getAccount(accountId));
    }
}