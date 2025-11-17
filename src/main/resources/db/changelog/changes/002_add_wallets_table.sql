--liquibase formatted sql
-- changeset nunopinho:002_add_wallets_table

CREATE TABLE wallets
(
    id         UUID PRIMARY KEY,
    user_id    UUID NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

--rollback DROP TABLE wallets;
