ALTER TABLE products
    DROP COLUMN country_id;

ALTER TABLE variants_awaiting_approval
    ADD COLUMN country_id uuid;

ALTER TABLE product_variants
    ADD COLUMN country_id uuid;