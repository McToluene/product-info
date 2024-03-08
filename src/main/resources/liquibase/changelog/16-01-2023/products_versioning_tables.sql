

ALTER TABLE public.products DROP CONSTRAINT product_brand_fk;
ALTER TABLE public.products DROP CONSTRAINT product_manufacturer_fk;
ALTER TABLE public.products DROP CONSTRAINT products_categories_fk;
ALTER TABLE public.products DROP CONSTRAINT unique_product_name;

ALTER TABLE public.products  DROP COLUMN brand_id;
ALTER TABLE public.products  DROP COLUMN manufacturer_id;
ALTER TABLE public.products  DROP COLUMN category_id;
ALTER TABLE public.products  DROP COLUMN measurement_unit;
ALTER TABLE public.products  DROP COLUMN product_name;
ALTER TABLE public.products  DROP COLUMN product_listing;
ALTER TABLE public.products  DROP COLUMN default_image_url;
ALTER TABLE public.products  DROP COLUMN product_description;
ALTER TABLE public.products  DROP COLUMN product_highlights;
ALTER TABLE public.products  DROP COLUMN warranty_duration;
ALTER TABLE public.products  DROP COLUMN warranty_cover;
ALTER TABLE public.products  DROP COLUMN warranty_type;
ALTER TABLE public.products  DROP COLUMN warranty_address;
ALTER TABLE public.products  DROP COLUMN product_country;



CREATE TABLE IF NOT EXISTS public.products_version
(
    id uuid NOT NULL,
    product_id uuid NOT NULL,
    brand_id uuid NOT NULL,
    manufacturer_id uuid NOT NULL,
    category_id uuid NOT NULL,
    measurement_unit character varying(100),
    product_name  character varying(255) NOT NULL,
    product_listing  character varying(100),
    default_image_url  character varying(500),
    product_description character varying(500),
    product_highlights text,
    product_notes text,
	warranty_duration  character varying(100),
	warranty_cover  character varying(255),
	warranty_type  character varying(100),
	warranty_address  character varying(255),
	product_country  character varying(255),
    created_date TIMESTAMP NOT NULL,
    created_by character varying(75) NOT NULL,
    last_modified_by character varying(75),
    approved_by character varying(75),
    approved_date TIMESTAMP,
	version bigint DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT product_public_id_fk FOREIGN KEY (product_id) REFERENCES public.products(id),
    CONSTRAINT product_brand_fk FOREIGN KEY (brand_id) REFERENCES public.brands(id),
    CONSTRAINT product_manufacturer_fk FOREIGN KEY (manufacturer_id) REFERENCES public.manufacturers(id),
    CONSTRAINT products_categories_fk FOREIGN KEY (category_id) REFERENCES public.product_categories(id)
    );
ALTER TABLE public.products_awaiting_approval  DROP COLUMN product_listing;

ALTER TABLE public.products_awaiting_approval
  ADD COLUMN is_new BOOLEAN NOT NULL DEFAULT TRUE;

  ALTER TABLE public.products_awaiting_approval
  ADD COLUMN product_listing character varying(100);

  ALTER TABLE public.products_awaiting_approval
  ADD COLUMN product_public_id uuid;

  ALTER TABLE public.image_catalog
  ADD COLUMN product_await_approval_id uuid;

    ALTER TABLE public.variants
  ADD COLUMN product_await_approval_id uuid;

ALTER TABLE public.variants
    ADD  CONSTRAINT product_v_await_appr_fk FOREIGN KEY (product_await_approval_id) REFERENCES public.products_awaiting_approval(id);

ALTER TABLE public.image_catalog
    ADD  CONSTRAINT product_img_await_appr_fk FOREIGN KEY (product_await_approval_id) REFERENCES public.products_awaiting_approval(id);

  ALTER TABLE public.image_catalog ALTER COLUMN product_id DROP NOT NULL;

  ALTER TABLE public.variants ALTER COLUMN product_id DROP NOT NULL;



