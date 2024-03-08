ALTER TABLE products_awaiting_approval
DROP COLUMN if exists warranty_type;

ALTER TABLE products_version
DROP COLUMN if exists warranty_type;
