UPDATE product_variants
SET vat_value = 0
WHERE vat_value IS NULL;