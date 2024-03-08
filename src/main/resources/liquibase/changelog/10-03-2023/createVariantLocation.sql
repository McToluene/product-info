CREATE TABLE IF NOT EXISTS public.variant_location
(
    id uuid NOT NULL PRIMARY KEY,
    public_id uuid NOT NULL,
    variant_public_id uuid NOT NULL,
    location_public_id uuid NOT NULL,
    created_date TIMESTAMP NOT NULL,
    created_by character varying(75) NOT NULL,
    last_modified_by character varying(75),
    last_modified_date TIMESTAMP,
    version bigint DEFAULT 0,
    status character varying(30)
);