CREATE TABLE IF NOT EXISTS public.brands
(
    id uuid NOT NULL,
    public_id uuid NOT NULL,
    description character varying(255),
    brand_name character varying(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    created_by character varying(75) NOT NULL,
    last_modified_by character varying(75),
    last_modified_date TIMESTAMP,
    status character varying(30),
    version bigint DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT unique_brand_name UNIQUE (brand_name),
    CONSTRAINT unique_brand_public_id UNIQUE (public_id)
    );

CREATE TABLE IF NOT EXISTS public.manufacturers
(
    id uuid NOT NULL,
    public_id uuid NOT NULL,
    description character varying(255),
    manufacturer_name character varying(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    created_by character varying(75) NOT NULL,
    last_modified_by character varying(75),
    last_modified_date TIMESTAMP,
    status character varying(30),
    version bigint DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT unique_manufacturer_name UNIQUE (manufacturer_name),
    CONSTRAINT unique_manufacturer_public_id UNIQUE (public_id)
    );

CREATE TABLE IF NOT EXISTS public.product_categories
(
    id uuid NOT NULL,
    public_id uuid NOT NULL,
    description character varying(255),
    product_category_name character varying(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    created_by character varying(75) NOT NULL,
    last_modified_by character varying(75),
    last_modified_date TIMESTAMP,
    status character varying(30),
    version bigint DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT unique_product_category_name UNIQUE (product_category_name),
    CONSTRAINT unique_product_category_public_id UNIQUE (public_id)
    );

CREATE TABLE IF NOT EXISTS public.variant_types
(
    id uuid NOT NULL,
    public_id uuid NOT NULL,
    description character varying(255),
    variant_type_name character varying(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    created_by character varying(75) NOT NULL,
    last_modified_by character varying(75),
    last_modified_date TIMESTAMP,
    status character varying(30),
    version bigint DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT unique_variant_type_name UNIQUE (variant_type_name),
    CONSTRAINT unique_variant_type_public_id UNIQUE (public_id)
    );
