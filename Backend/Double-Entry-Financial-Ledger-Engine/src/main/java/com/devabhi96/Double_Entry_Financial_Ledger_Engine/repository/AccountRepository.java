package com.devabhi96.Double_Entry_Financial_Ledger_Engine.repository;

import com.devabhi96.Double_Entry_Financial_Ledger_Engine.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
}
