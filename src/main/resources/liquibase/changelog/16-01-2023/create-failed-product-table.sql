CREATE TABLE IF NOT EXISTS public.failed_products
(
    id uuid NOT NULL,
    public_id uuid NOT NULL,
    brand_name character varying(255) NOT NULL,
    manufacturer_name character varying(255) NOT NULL,
    product_category_name character varying(255) NOT NULL,
    measurement_unit character varying(100),
    product_name  character varying(255) NOT NULL,
    product_listing  varchar(100) NULL,
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
    last_modified_date TIMESTAMP,
    status character varying(30),
    version bigint DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT failed_products_public_id UNIQUE (public_id)
    );