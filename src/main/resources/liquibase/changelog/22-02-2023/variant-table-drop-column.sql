ALTER TABLE public.variants
DROP COLUMN product_id;

ALTER TABLE public.variants
ADD product_id varchar (100);