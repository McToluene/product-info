ALTER TABLE product_variants
    ADD COLUMN if not exists variant_description varchar(255);