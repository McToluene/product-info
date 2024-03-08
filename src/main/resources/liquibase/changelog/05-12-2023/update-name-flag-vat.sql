ALTER TABLE public.product_variants drop column if exists isVated;

ALTER TABLE public.product_variants
    ADD COLUMN if not exists vated boolean;