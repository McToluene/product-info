
ALTER TABLE public.variants_awaiting_approval
ADD product_id uuid;

ALTER TABLE public.variants_awaiting_approval
ADD CONSTRAINT product_id_fk FOREIGN KEY (product_id) REFERENCES public.products(id);