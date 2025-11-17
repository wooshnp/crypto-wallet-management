--liquibase formatted sql
-- changeset nunopinho:004_add_price_history_table

CREATE TABLE price_history
(
    id        UUID PRIMARY KEY,
    symbol    VARCHAR(20) NOT NULL,
    price     DECIMAL(20, 2) NOT NULL,
    created_at DATE NOT NULL
);

CREATE INDEX idx_symbol_timestamp ON price_history (symbol, created_at);
-- rollback DROP TABLE price_history;
-- rollback DROP INDEX idx_symbol_timestamp;