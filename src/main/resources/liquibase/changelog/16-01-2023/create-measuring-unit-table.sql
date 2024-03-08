CREATE TABLE IF NOT EXISTS public.measuring_unit
(
    id uuid NOT NULL,
    public_id uuid NOT NULL,
    name character varying(255) NOT NULL,
    abbreviation character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    description character varying(255),
    created_date TIMESTAMP NOT NULL,
    created_by character varying(75) NOT NULL,
    last_modified_by character varying(75),
    last_modified_date TIMESTAMP,
    version bigint DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT unique_measuring_unit_public_id UNIQUE (public_id),
    CONSTRAINT unique_measuring_unit_name UNIQUE (name),
    CONSTRAINT unique_measuring_unit_abbreviation UNIQUE (abbreviation)
    );

