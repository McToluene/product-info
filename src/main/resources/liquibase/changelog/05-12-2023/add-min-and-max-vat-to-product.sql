ALTER TABLE public.products
    ADD COLUMN if not exists min_vat DECIMAL;

ALTER TABLE public.products
    ADD COLUMN if not exists max_vat DECIMAL;

UPDATE products
SET min_vat = 0
WHERE min_vat IS NULL;

UPDATE products
SET max_vat = 0
WHERE max_vat IS NULL;