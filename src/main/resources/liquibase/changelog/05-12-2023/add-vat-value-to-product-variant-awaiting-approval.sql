ALTER TABLE public.variants_awaiting_approval
    ADD COLUMN if not exists vat_value DECIMAL;