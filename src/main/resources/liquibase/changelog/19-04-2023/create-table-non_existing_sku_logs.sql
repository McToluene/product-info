CREATE TABLE IF NOT EXISTS public.non_existing_sku_logs
(
    id uuid NOT NULL,
    sku String NOT NULL,
    created_date TIMESTAMP NOT NULL,
    created_by character varying(75) NOT NULL,
    PRIMARY KEY (id)
);
