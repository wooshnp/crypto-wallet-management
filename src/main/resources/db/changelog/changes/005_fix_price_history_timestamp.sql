--liquibase formatted sql
-- changeset nunopinho:005_fix_price_history_timestamp

ALTER TABLE price_history ALTER COLUMN created_at TYPE TIMESTAMP;

-- rollback ALTER TABLE price_history ALTER COLUMN created_at TYPE DATE;
