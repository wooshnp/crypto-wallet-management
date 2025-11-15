--liquibase formatted sql
-- changeset nunopinho:003_add_assets_table

CREATE TABLE assets
(
    id            UUID PRIMARY KEY,
    wallet_id     UUID NOT NULL,
    symbol        VARCHAR(20) NOT NULL,
    quantity      DECIMAL(20, 8) NOT NULL,
    current_price DECIMAL(20, 2) NOT NULL,
    created_at    TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP,
    CONSTRAINT fk_asset_wallet FOREIGN KEY (wallet_id) REFERENCES wallets (id) ON DELETE CASCADE
);

-- rollback DROP TABLE assets;