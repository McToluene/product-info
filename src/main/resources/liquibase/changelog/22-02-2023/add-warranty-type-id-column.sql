ALTER TABLE products_awaiting_approval
    ADD warranty_type_id uuid;

ALTER TABLE products_version
    ADD warranty_type_id uuid;