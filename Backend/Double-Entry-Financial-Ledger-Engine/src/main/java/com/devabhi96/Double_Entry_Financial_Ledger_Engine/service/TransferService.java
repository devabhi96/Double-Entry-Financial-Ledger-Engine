package com.devabhi96.Double_Entry_Financial_Ledger_Engine.service;

import com.devabhi96.Double_Entry_Financial_Ledger_Engine.dto.TransferRequest;
import com.devabhi96.Double_Entry_Financial_Ledger_Engine.dto.TransferResponse;
import com.devabhi96.Double_Entry_Financial_Ledger_Engine.entity.*;
import com.devabhi96.Double_Entry_Financial_Ledger_Engine.exception.*;
import com.devabhi96.Double_Entry_Financial_Ledger_Engine.repository.AccountRepository;
import com.devabhi96.Double_Entry_Financial_Ledger_Engine.repository.LedgerEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final IdempotencyService idempotencyService;

    public TransferService(AccountRepository accountRepository,
                           LedgerEntryRepository ledgerEntryRepository,
                           IdempotencyService idempotencyService) {
        this.accountRepository = accountRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.idempotencyService = idempotencyService;
    }

    public record Result(TransferResponse body, boolean isReplay) {}

    @Transactional
    public Result transfer(TransferRequest req, String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IdempotencyKeyMissingException();
        }
        String redisKey = "idem:" + idempotencyKey;
        String requestHash = idempotencyService.hash(req.fromAccountId() + "|" + req.toAccountId() + "|" + req.amount());

        String existing = idempotencyService.get(redisKey);
        if (existing != null) {
            return handleExisting(existing, requestHash);
        }
        if (!idempotencyService.tryClaim(redisKey, requestHash)) {

            existing = idempotencyService.get(redisKey);
            if (existing != null) return handleExisting(existing, requestHash);
            throw new IdempotencyConflictException("Request with this key is already being processed.");
        }

        try {
            TransferResponse response = doTransfer(req);
            idempotencyService.complete(redisKey, "COMPLETED|" + response.txnId() + "|" + requestHash);
            return new Result(response, false);
        } catch (RuntimeException ex) {
            idempotencyService.release(redisKey);
            throw ex;
        }
    }

    private Result handleExisting(String record, String requestHash) {
        String[] parts = record.split("\\|", 3);
        String status = parts[0];
        if ("PROCESSING".equals(status)) {
            throw new IdempotencyConflictException("Request with this key is already being processed.");
        }

        String storedHash = parts[2];
        if (!storedHash.equals(requestHash)) {
            throw new IdempotencyConflictException("Idempotency key reused with a different request payload.");
        }
        UUID txnId = UUID.fromString(parts[1]);
        var entries = ledgerEntryRepository.findByTxnId(txnId);
        var debit = entries.stream().filter(e -> e.getType() == EntryType.DEBIT).findFirst().orElseThrow();
        var credit = entries.stream().filter(e -> e.getType() == EntryType.CREDIT).findFirst().orElseThrow();
        TransferResponse body = TransferResponse.of(txnId, debit.getAccountId(), credit.getAccountId(),
                debit.getAmount(), debit.getCreatedAt());
        return new Result(body, true);
    }

    private TransferResponse doTransfer(TransferRequest req) {
        if (req.amount() == null || req.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException();
        }
        if (req.fromAccountId().equals(req.toAccountId())) {
            throw new InvalidTransferException("Source and destination account must differ.");
        }


        UUID first = req.fromAccountId().compareTo(req.toAccountId()) < 0 ? req.fromAccountId() : req.toAccountId();
        UUID second = first.equals(req.fromAccountId()) ? req.toAccountId() : req.fromAccountId();

        Account firstLocked = accountRepository.findByIdForUpdate(first)
                .orElseThrow(() -> new AccountNotFoundException(first));
        Account secondLocked = accountRepository.findByIdForUpdate(second)
                .orElseThrow(() -> new AccountNotFoundException(second));

        Account from = firstLocked.getId().equals(req.fromAccountId()) ? firstLocked : secondLocked;
        Account to = firstLocked.getId().equals(req.toAccountId()) ? firstLocked : secondLocked;

        if (from.getStatus() != AccountStatus.ACTIVE) throw new AccountFrozenException(from.getId());
        if (to.getStatus() != AccountStatus.ACTIVE) throw new AccountFrozenException(to.getId());

        if (from.getBalance().compareTo(req.amount()) < 0) {
            throw new InsufficientFundsException(from.getId());
        }

        BigDecimal newFromBalance = from.getBalance().subtract(req.amount());
        BigDecimal newToBalance = to.getBalance().add(req.amount());
        from.setBalance(newFromBalance);
        to.setBalance(newToBalance);
        accountRepository.save(from);
        accountRepository.save(to);

        UUID txnId = UUID.randomUUID();
        ledgerEntryRepository.save(new LedgerEntry(txnId, from.getId(), EntryType.DEBIT, req.amount(), newFromBalance));
        ledgerEntryRepository.save(new LedgerEntry(txnId, to.getId(), EntryType.CREDIT, req.amount(), newToBalance));

        return TransferResponse.of(txnId, from.getId(), to.getId(), req.amount(), Instant.now());
    }
}