ALTER TABLE public.image_catalog
    ADD variant_await_approval_id uuid;

ALTER TABLE public.image_catalog
    ADD CONSTRAINT variant_await_approval_fk
    FOREIGN KEY (variant_await_approval_id) REFERENCES public.variants_awaiting_approval(id);