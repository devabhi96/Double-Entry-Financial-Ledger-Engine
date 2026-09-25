package com.devabhi96.Double_Entry_Financial_Ledger_Engine.controller;

import com.devabhi96.Double_Entry_Financial_Ledger_Engine.dto.TransferDetailResponse;
import com.devabhi96.Double_Entry_Financial_Ledger_Engine.dto.TransferRequest;
import com.devabhi96.Double_Entry_Financial_Ledger_Engine.dto.TransferResponse;
import com.devabhi96.Double_Entry_Financial_Ledger_Engine.exception.TransferNotFoundException;
import com.devabhi96.Double_Entry_Financial_Ledger_Engine.repository.LedgerEntryRepository;
import com.devabhi96.Double_Entry_Financial_Ledger_Engine.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class TransferController {

    private final TransferService transferService;
    private final LedgerEntryRepository ledgerEntryRepository;

    public TransferController(TransferService transferService, LedgerEntryRepository ledgerEntryRepository) {
        this.transferService = transferService;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @PostMapping("/transfers")
    public ResponseEntity<TransferResponse> create(@Valid @RequestBody TransferRequest request,
                                                   @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        TransferService.Result result = transferService.transfer(request, idempotencyKey);
        return ResponseEntity.status(result.isReplay() ? HttpStatus.OK : HttpStatus.CREATED).body(result.body());
    }

    @GetMapping("/transfers/{txnId}")
    public TransferDetailResponse get(@PathVariable UUID txnId) {
        var entries = ledgerEntryRepository.findByTxnId(txnId);
        if (entries.isEmpty()) throw new TransferNotFoundException(txnId);
        return TransferDetailResponse.from(txnId, entries);
    }
}