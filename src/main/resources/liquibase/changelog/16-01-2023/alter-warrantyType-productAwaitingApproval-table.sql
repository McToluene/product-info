ALTER TABLE products_awaiting_approval
RENAME COLUMN warranty_type to warranty_type_id;

Alter Table products_awaiting_approval
Alter Column  warranty_type_id TYPE uuid USING warranty_type_id::uuid;