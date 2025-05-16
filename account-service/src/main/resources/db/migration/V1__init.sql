CREATE TABLE IF NOT EXISTS accounts
(
    id          UUID PRIMARY KEY,
    iban        VARCHAR(40) NOT NULL,
    bic_swift   VARCHAR(40) NOT NULL,
    customer_id UUID        NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_accounts_customer_id ON accounts (customer_id);