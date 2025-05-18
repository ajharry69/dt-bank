CREATE TABLE IF NOT EXISTS cards
(
    id                 UUID PRIMARY KEY,
    alias              VARCHAR(40) NOT NULL,
    pan                VARCHAR(40) NOT NULL,
    cvv                VARCHAR(3)  NOT NULL,
    type               VARCHAR(20) NOT NULL,
    date_created       TIMESTAMP WITH TIME ZONE,
    date_last_modified TIMESTAMP WITH TIME ZONE,
    account_id         UUID        NOT NULL,
    UNIQUE (account_id, type) -- database constraint will act as a last resort if code enforcement fails
);

CREATE INDEX IF NOT EXISTS idx_cards_account_id ON cards (account_id);
CREATE INDEX IF NOT EXISTS idx_cards_date_created ON cards (date_created);
CREATE INDEX IF NOT EXISTS idx_cards_date_last_modified ON cards (date_last_modified);