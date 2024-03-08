CREATE TABLE IF NOT EXISTS public.variants_awaiting_approval
(
    id uuid NOT NULL,
    public_id uuid NOT NULL,
    variant_type_id uuid NOT NULL,
    variant_name character varying(255) NOT NULL,
    variant_description character varying(500),
    default_image_url  character varying(500),
    cost_price DECIMAL,
    sku character varying(100),
    created_date TIMESTAMP NOT NULL,
    created_by character varying(75) NOT NULL,
    completed_date TIMESTAMP NOT NULL,
    completed_by character varying(75) NOT NULL,
    last_modified_by character varying(75),
    last_modified_date TIMESTAMP,
    status character varying(30),
    approval_status character varying(30),
    version bigint DEFAULT 0,
    is_new BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    CONSTRAINT variants_awaiting_approval_fk FOREIGN KEY (variant_type_id) REFERENCES public.variant_types(id),
    CONSTRAINT variants_awaiting_approval_sku_unique_constraint UNIQUE (sku, version, approval_status),
    CONSTRAINT unique_variants_awaiting_approval_public_id UNIQUE (public_id)
    );



CREATE TABLE IF NOT EXISTS public.variants_version
(
    id uuid NOT NULL,
    public_id uuid NOT NULL,
    product_id uuid NOT NULL,
    variant_type_id uuid NOT NULL,
    variant_name character varying(255) NOT NULL,
    default_image_url  character varying(500),
    cost_price DECIMAL,
    variant_description character varying(500),
    sku character varying(100),
    status character varying(30),
    created_date TIMESTAMP NOT NULL,
    created_by character varying(75) NOT NULL,
    last_modified_by character varying(75),
    last_modified_date TIMESTAMP,
    approved_by character varying(75),
    approved_date TIMESTAMP,
	version bigint DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT product_public_id_fk FOREIGN KEY (product_id) REFERENCES public.products(id),
    CONSTRAINT variants_version_fk FOREIGN KEY (variant_type_id) REFERENCES public.variant_types(id),
    CONSTRAINT variant_sku_unique_constraint UNIQUE (sku, version),
    CONSTRAINT unique_variants_version_public_id UNIQUE (public_id)
    );

CREATE TABLE IF NOT EXISTS public.product_variants
(
    id uuid NOT NULL,
    public_id uuid NOT NULL,
    status character varying(30),
    created_date TIMESTAMP NOT NULL,
    created_by character varying(75) NOT NULL,
    last_modified_by character varying(75),
    last_modified_date TIMESTAMP,
    approved_by character varying(75),
    approved_date TIMESTAMP,
    version bigint DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT unique_product_variants_public_id UNIQUE (public_id)
    );
