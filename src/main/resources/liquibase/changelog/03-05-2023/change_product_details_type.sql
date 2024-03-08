ALTER TABLE variants_awaiting_approval
    ALTER COLUMN product_variant_details TYPE TEXT;

ALTER TABLE failed_products
    ADD COLUMN product_details TEXT;