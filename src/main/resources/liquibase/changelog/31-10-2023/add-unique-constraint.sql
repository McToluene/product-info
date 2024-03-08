ALTER TABLE product_variants
    ADD CONSTRAINT unique_product_id_name_country_id UNIQUE (product_id, name, country_id);