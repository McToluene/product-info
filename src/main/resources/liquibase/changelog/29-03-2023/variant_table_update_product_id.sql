ALTER TABLE variants
DROP COLUMN product_id;

ALTER TABLE variants
ADD COLUMN product_id uuid;