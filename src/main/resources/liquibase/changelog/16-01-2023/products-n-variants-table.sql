

CREATE TABLE IF NOT EXISTS public.products
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
    last_modified_date TIMESTAMP,
    status character varying(30),
    version bigint DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT product_brand_fk FOREIGN KEY (brand_id) REFERENCES public.brands(id),
    CONSTRAINT product_manufacturer_fk FOREIGN KEY (manufacturer_id) REFERENCES public.manufacturers(id),
    CONSTRAINT products_categories_fk FOREIGN KEY (category_id) REFERENCES public.product_categories(id),
    CONSTRAINT unique_product_name UNIQUE (product_name),
    CONSTRAINT unique_product_public_id UNIQUE (public_id)
    );

CREATE TABLE IF NOT EXISTS public.image_catalog
(
    id uuid NOT NULL,
    public_id uuid NOT NULL,
    product_id uuid NOT NULL,
    image_url  character varying(500) NOT NULL,
    image_description character varying(500),
    created_date TIMESTAMP NOT NULL,
    created_by character varying(75) NOT NULL,
    last_modified_by character varying(75),
    last_modified_date TIMESTAMP,
    status character varying(30),
    version bigint DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT products_image_fk FOREIGN KEY (product_id) REFERENCES public.products(id),
    CONSTRAINT unique_image_catalog_public_id UNIQUE (public_id)
    );

CREATE TABLE IF NOT EXISTS public.variants
(
    id uuid NOT NULL,
    public_id uuid NOT NULL,
    product_id uuid NOT NULL,
    variant_type_id uuid NOT NULL,
    variant_name  character varying(255) NOT NULL,
    variant_description character varying(500),
    sku character varying(100),
    cost_price numeric,
    quantity int4,
    created_date TIMESTAMP NOT NULL,
    created_by character varying(75) NOT NULL,
    last_modified_by character varying(75),
    last_modified_date TIMESTAMP,
    status character varying(30),
    version bigint DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT variants_fk FOREIGN KEY (variant_type_id) REFERENCES public.variant_types(id),
    CONSTRAINT products_variants_fk FOREIGN KEY (product_id) REFERENCES public.products(id),
	CONSTRAINT unique_variant_name UNIQUE (variant_name),
    CONSTRAINT unique_variants_public_id UNIQUE (public_id)
    );


















