ALTER TABLE variants_awaiting_approval ADD COLUMN IF NOT EXISTS product_variant_id uuid;

ALTER TABLE variants_awaiting_approval ADD CONSTRAINT variants_awaiting_approval_variant_fk FOREIGN KEY (product_variant_id) REFERENCES public.product_variants(id)