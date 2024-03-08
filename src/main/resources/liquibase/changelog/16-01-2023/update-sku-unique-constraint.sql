ALTER TABLE variants DROP CONSTRAINT variant_sku_unique_constraint;

ALTER TABLE variants ADD CONSTRAINT variant_sku_unique_constraint UNIQUE (sku, version);