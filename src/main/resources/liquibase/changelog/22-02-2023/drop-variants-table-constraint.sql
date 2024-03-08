ALTER TABLE public.variants
DROP CONSTRAINT products_variants_fk;

ALTER TABLE public.variants ALTER COLUMN product_id DROP NOT NULL;