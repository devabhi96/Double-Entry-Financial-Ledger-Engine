CREATE TABLE ledger_entries (
                                id BIGSERIAL PRIMARY KEY,
                                txn_id UUID NOT NULL,
                                account_id UUID NOT NULL REFERENCES accounts(id),
                                type VARCHAR(10) NOT NULL CHECK (type IN ('DEBIT', 'CREDIT')),
                                amount NUMERIC(19, 4) NOT NULL CHECK (amount > 0),
                                balance_after NUMERIC(19, 4) NOT NULL,
                                created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_ledger_txn_id ON ledger_entries(txn_id);
CREATE INDEX idx_ledger_account_id_created_at ON ledger_entries(account_id, created_at);

CREATE TABLE idempotency_keys (
                                  idempotency_key VARCHAR(255) PRIMARY KEY,
                                  request_hash VARCHAR(255) NOT NULL,
                                  txn_id UUID NOT NULL,
                                  status VARCHAR(20) NOT NULL,
                                  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);