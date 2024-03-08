ALTER TABLE public.product_variants
    ADD COLUMN if not exists vat_value DECIMAL;