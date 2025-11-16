--liquibase formatted sql
-- changeset nunopinho:006_add_acquisition_price_to_assets

ALTER TABLE assets
    ADD COLUMN acquisition_price DECIMAL(20, 2) NOT NULL DEFAULT 0;

UPDATE assets SET acquisition_price = current_price WHERE acquisition_price = 0;

-- rollback ALTER TABLE assets DROP COLUMN acquisition_price;
