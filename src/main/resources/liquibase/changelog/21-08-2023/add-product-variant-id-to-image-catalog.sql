ALTER TABLE image_catalog
ADD COLUMN product_variant_id uuid;

ALTER TABLE image_catalog
ADD CONSTRAINT product_fk FOREIGN KEY (product_variant_id) REFERENCES public.product_variants(id);