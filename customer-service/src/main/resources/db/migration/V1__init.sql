CREATE TABLE IF NOT EXISTS customers
(
    id         UUID PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    other_name VARCHAR(255)
);