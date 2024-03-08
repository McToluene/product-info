ALTER TABLE product_variants ADD COLUMN name character varying(75);

UPDATE product_variants pv
SET name =  vv.variant_name
    FROM (SELECT variant_name, product_variant_id
      FROM variants_version AS T1
      WHERE version=(SELECT MAX(version) FROM variants_version AS T2
                                         WHERE T1.product_variant_id = T2.product_variant_id)) AS vv
WHERE pv.id = vv.product_variant_id;

