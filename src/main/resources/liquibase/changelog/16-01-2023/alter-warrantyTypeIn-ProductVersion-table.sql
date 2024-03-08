ALTER TABLE products_version
RENAME COLUMN warranty_type to warranty_type_id;

Alter Table products_version
Alter Column  warranty_type_id TYPE uuid USING warranty_type_id::uuid;