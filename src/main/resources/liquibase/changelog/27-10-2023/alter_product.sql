
ALTER TABLE products
    ADD CONSTRAINT unique_product_brand_manufacturer UNIQUE (product_name, brand_id, manufacturer_id);

CREATE UNIQUE INDEX idx_unique_product_brand_manufacturer ON products (product_name, brand_id, manufacturer_id);
