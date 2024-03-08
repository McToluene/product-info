CREATE TABLE IF NOT EXISTS public.image
(
    id uuid NOT NULL,
    public_id uuid NOT NULL,
    url character varying(255),
    created_date TIMESTAMP NOT NULL,
    created_by character varying(75) NOT NULL,
    last_modified_by character varying(75),
    last_modified_date TIMESTAMP,
    status character varying(30),
    version bigint DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT unique_image_public_id UNIQUE (public_id)
    );