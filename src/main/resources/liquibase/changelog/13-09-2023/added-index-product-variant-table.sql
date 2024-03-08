CREATE INDEX product_variant_product_id
    ON product_variants (product_id);

CREATE INDEX product_variant_publicId_index
    ON product_variants (status, public_id);

CREATE INDEX product_variant_status_index
    ON product_variants (status);