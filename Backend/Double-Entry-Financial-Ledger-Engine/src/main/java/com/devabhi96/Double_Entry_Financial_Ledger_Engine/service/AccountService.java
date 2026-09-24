package com.devabhi96.Double_Entry_Financial_Ledger_Engine.service;

import com.devabhi96.Double_Entry_Financial_Ledger_Engine.entity.Account;
import com.devabhi96.Double_Entry_Financial_Ledger_Engine.exception.AccountNotFoundException;
import com.devabhi96.Double_Entry_Financial_Ledger_Engine.repository.AccountRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account createAccount(Long userId){
        return accountRepository.save(new Account(userId));
    }

    @Transactional(readOnly = true)
    public Account getAccount(UUID id){
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }


}
