ALTER TABLE public.variants_awaiting_approval
    drop column if exists isVated;

ALTER TABLE public.variants_awaiting_approval
    ADD COLUMN if not exists vated boolean;