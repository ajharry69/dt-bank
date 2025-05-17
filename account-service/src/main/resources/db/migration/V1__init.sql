CREATE TABLE IF NOT EXISTS accounts
(
    id                 UUID PRIMARY KEY,
    iban               VARCHAR(40) NOT NULL,
    bic_swift          VARCHAR(40) NOT NULL,
    date_created       TIMESTAMP WITH TIME ZONE,
    date_last_modified TIMESTAMP WITH TIME ZONE,
    customer_id        UUID        NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_accounts_customer_id ON accounts (customer_id);
CREATE INDEX IF NOT EXISTS idx_accounts_date_created ON accounts (date_created);
CREATE INDEX IF NOT EXISTS idx_accounts_date_last_modified ON accounts (date_last_modified);