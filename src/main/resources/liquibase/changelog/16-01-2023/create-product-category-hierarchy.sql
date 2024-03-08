CREATE TABLE IF NOT EXISTS public.product_category_hierarchy
(
    id uuid NOT NULL,
    public_id uuid NOT NULL,
    product_category_public_id uuid NOT NULL,
    product_category_parent_public_id uuid,
    created_date TIMESTAMP NOT NULL,
    created_by character varying(75) NOT NULL,
    last_modified_by character varying(75),
    last_modified_date TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT unique_product_category_hierarchy_public_id UNIQUE (public_id)
    );