CREATE TABLE IF NOT EXISTS public.warranty_type
(
    id uuid NOT NULL,
    public_id uuid NOT NULL,
    warranty_type_name  character varying(255) NOT NULL,
    description character varying(500),
    created_date TIMESTAMP NOT NULL,
    created_by character varying(75) NOT NULL,
    last_modified_by character varying(75),
    last_modified_date TIMESTAMP,
    status character varying(30),
    version bigint DEFAULT 0,
    PRIMARY KEY (id)

);