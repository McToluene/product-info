CREATE TABLE IF NOT EXISTS public.products_awaiting_approval
(
    id uuid NOT NULL,
    public_id uuid NOT NULL,
    brand_id uuid NOT NULL,
    manufacturer_id uuid NOT NULL,
    category_id uuid NOT NULL,
    measurement_unit character varying(100),
    product_name  character varying(255) NOT NULL,
    product_listing  numeric default 0,
    default_image_url  character varying(500),
    product_description character varying(500),
    product_highlights text,
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
    last_modified_date TIMESTAMP,
    approval_status character varying(30),
    status character varying(30),
    version bigint DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT products_awaiting_approval_brand_fk FOREIGN KEY (brand_id) REFERENCES public.brands(id),
    CONSTRAINT products_awaiting_approval_manufacturer_fk FOREIGN KEY (manufacturer_id) REFERENCES public.manufacturers(id),
    CONSTRAINT products_awaiting_approval_categories_fk FOREIGN KEY (category_id) REFERENCES public.product_categories(id),
    CONSTRAINT unique_products_awaiting_approval_public_id UNIQUE (public_id)
    );
	ALTER TABLE products
    ADD approved_by character varying(75);

	ALTER TABLE products
    ADD approved_date TIMESTAMP;