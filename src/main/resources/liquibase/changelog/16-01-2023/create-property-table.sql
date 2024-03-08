CREATE TABLE IF NOT EXISTS public.property
(
    id Integer NOT NULL,
    public_id uuid NOT NULL,
    name character varying(255) NOT NULL,
    value character varying(255) NOT NULL,
    description character varying(255),
    created_date TIMESTAMP NOT NULL,
    created_by character varying(75) NOT NULL,
    last_modified_by character varying(75),
    last_modified_date TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT unique_property_public_id UNIQUE (public_id),
    CONSTRAINT unique_property_name UNIQUE (name)
    );