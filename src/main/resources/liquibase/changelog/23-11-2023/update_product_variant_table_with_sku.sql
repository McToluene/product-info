ALTER TABLE product_variants ADD COLUMN IF NOT EXISTS variant_type_id uuid;

ALTER TABLE product_variants
    ADD CONSTRAINT fk_variant_type FOREIGN KEY (variant_type_id) REFERENCES variant_types
        (id);


UPDATE product_variants pv
SET sku =  vv.sku
    FROM (SELECT sku, product_variant_id
      FROM variants_version AS T1
      WHERE version=(SELECT MAX(version) FROM variants_version AS T2
                                         WHERE T1.product_variant_id = T2.product_variant_id)) AS vv
WHERE pv.id = vv.product_variant_id;


UPDATE product_variants pv
SET variant_type_id =  vv.variant_type_id
    FROM (SELECT variant_type_id, product_variant_id
      FROM variants_version AS T1
      WHERE version=(SELECT MAX(version) FROM variants_version AS T2
                                         WHERE T1.product_variant_id = T2.product_variant_id)) AS vv
WHERE pv.id = vv.product_variant_id;