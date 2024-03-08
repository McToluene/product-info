ALTER TABLE variants
DROP COLUMN IF EXISTS product_await_approval_id;

ALTER TABLE image_catalog
DROP COLUMN IF EXISTS product_await_approval_id;



DROP TABLE IF EXISTS products_awaiting_approval;

DROP TABLE IF EXISTS products_version;

DROP TABLE IF EXISTS products cascade;



CREATE TABLE IF NOT EXISTS public.products
(
        id uuid NOT NULL,
        public_id uuid NOT NULL,
        brand_id uuid NOT NULL,
        manufacturer_id uuid NOT NULL,
        category_id uuid NOT NULL,
        measuring_unit_id uuid,
        product_name character varying(255) NOT NULL,
        product_listing character varying(100),
        default_image_url character varying(500),
        product_description character varying(500),
        product_highlights text,
        product_notes text,
        warranty_duration character varying(100),
        warranty_cover character varying(255),
        warranty_type_id uuid,
        warranty_address text,
        country_id uuid,
        created_date TIMESTAMP NOT NULL,
        created_by character varying(75) NOT NULL,
        last_modified_date TIMESTAMP,
        last_modified_by character varying(75),
        status character varying(30),
        version bigint DEFAULT 0,

        PRIMARY KEY (id),
        CONSTRAINT product_brand_fk FOREIGN KEY (brand_id) REFERENCES public.brands(id),
        CONSTRAINT product_manufacturer_fk FOREIGN KEY (manufacturer_id) REFERENCES public.manufacturers(id),
        CONSTRAINT products_categories_fk FOREIGN KEY (category_id) REFERENCES public.product_categories(id),
        CONSTRAINT unique_products UNIQUE (public_id, status, version)
        );

--
-- ALTER TABLE image_catalog
-- ADD CONSTRAINT product_fk FOREIGN KEY (product_variant_id) REFERENCES public.product_variants(id);

