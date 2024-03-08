ALTER TABLE brands
ADD COLUMN manufacturer_id uuid,
   ADD CONSTRAINT unique_brand_manufacturer UNIQUE (brand_name, manufacturer_id);