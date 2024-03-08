ALTER TABLE variants DROP CONSTRAINT variant_sku_unique_constraint;
ALTER TABLE variants DROP CONSTRAINT unique_variant_name;


ALTER TABLE variants ADD CONSTRAINT variant_name_sku_version_unique_constraint UNIQUE (variant_name, sku, version);