CREATE TABLE IF NOT EXISTS customers
(
    id                 UUID PRIMARY KEY,
    first_name         VARCHAR(255) NOT NULL,
    last_name          VARCHAR(255) NOT NULL,
    other_name         VARCHAR(255),
    date_created       TIMESTAMP WITH TIME ZONE,
    date_last_modified TIMESTAMP WITH TIME ZONE,
    searchable         tsvector GENERATED ALWAYS AS (
        to_tsvector(
                'english',
                coalesce(first_name, '') ||
                ' ' ||
                coalesce(last_name, '') ||
                ' ' ||
                coalesce(other_name, '')
        )) STORED
);

CREATE INDEX IF NOT EXISTS idx_customers_date_created ON customers (date_created);
CREATE INDEX IF NOT EXISTS idx_customers_date_last_modified ON customers (date_last_modified);