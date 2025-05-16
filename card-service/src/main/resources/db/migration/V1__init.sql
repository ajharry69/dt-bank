CREATE TABLE IF NOT EXISTS cards
(
    id         UUID PRIMARY KEY,
    alias      VARCHAR(40) NOT NULL,
    pan        VARCHAR(40) NOT NULL,
    cvv        VARCHAR(3)  NOT NULL,
    type       VARCHAR(20) NOT NULL,
    account_id UUID        NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_cards_account_id ON cards (account_id);