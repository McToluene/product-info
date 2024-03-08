ALTER TABLE public.product_variants
ADD original_public_id uuid;


ALTER TABLE public.product_variants
ADD product_id uuid NOT NULL;

ALTER TABLE public.product_variants
ADD CONSTRAINT product_id_fk FOREIGN KEY (product_id) REFERENCES public.products(id);


ALTER TABLE public.variants_version
ADD product_variant_id uuid NOT NULL;


ALTER TABLE public.variants_version
ADD CONSTRAINT product_variant_id_fk FOREIGN KEY (product_variant_id) REFERENCES public.product_variants(id);
