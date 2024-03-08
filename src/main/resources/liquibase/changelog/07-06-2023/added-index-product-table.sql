CREATE INDEX product_name_index
ON products (product_name);

CREATE INDEX product_publicId_index
ON products (status, public_id);

CREATE INDEX product_status_index
ON products (status);

CREATE INDEX product_category_status_index
ON products (status, category_id);

CREATE INDEX product_name_status_index
ON products (status, product_name);