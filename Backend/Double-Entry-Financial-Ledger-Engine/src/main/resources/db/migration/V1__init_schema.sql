-- ACCOUNTS
CREATE TABLE accounts (
                          id          UUID          PRIMARY KEY,
                          user_id     BIGINT        NOT NULL,
                          balance     NUMERIC(19,4) NOT NULL DEFAULT 0 CHECK (balance >= 0),
                          status      VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE'
                              CHECK (status IN ('ACTIVE', 'FROZEN', 'CLOSED')),
                          created_at  TIMESTAMPTZ   NOT NULL DEFAULT now(),
                          updated_at  TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_accounts_user_id ON accounts (user_id);

-- LEDGER ENTRIES (append-only)
CREATE TABLE ledger_entries (
                                id             BIGSERIAL     PRIMARY KEY,
                                txn_id         UUID          NOT NULL,
                                account_id     UUID          NOT NULL REFERENCES accounts (id),
                                type           VARCHAR(10)   NOT NULL CHECK (type IN ('DEBIT', 'CREDIT')),
                                amount         NUMERIC(19,4) NOT NULL CHECK (amount > 0),
                                balance_after  NUMERIC(19,4) NOT NULL,
                                created_at     TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_ledger_txn_id ON ledger_entries (txn_id);
CREATE INDEX idx_ledger_account_id_created_at ON ledger_entries (account_id, created_at);

-- Enforce immutability at the DB level (FR-8)
CREATE FUNCTION prevent_ledger_mutation() RETURNS trigger AS $$
BEGIN
    RAISE EXCEPTION 'ledger_entries is append-only: % is not allowed', TG_OP;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_ledger_no_update_delete
    BEFORE UPDATE OR DELETE ON ledger_entries
    FOR EACH ROW EXECUTE FUNCTION prevent_ledger_mutation();

-- IDEMPOTENCY KEYS (optional Postgres mirror of Redis)
CREATE TABLE idempotency_keys (
                                  idempotency_key  VARCHAR(255) PRIMARY KEY,
                                  request_hash     VARCHAR(64)  NOT NULL,
                                  txn_id           UUID,
                                  status           VARCHAR(20)  NOT NULL
                                      CHECK (status IN ('PROCESSING', 'COMPLETED', 'FAILED')),
                                  created_at       TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- ADMIN AUDIT LOG
CREATE TABLE admin_audit_log (
                                 id                 BIGSERIAL    PRIMARY KEY,
                                 admin_id           BIGINT       NOT NULL,
                                 action             VARCHAR(50)  NOT NULL,
                                 target_account_id  UUID,
                                 details            JSONB,
                                 created_at         TIMESTAMPTZ  NOT NULL DEFAULT now()
);