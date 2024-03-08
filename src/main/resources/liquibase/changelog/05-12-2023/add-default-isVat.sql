UPDATE product_variants
SET vated = false
WHERE vated IS NULL;

UPDATE variants_awaiting_approval
SET vated = false
WHERE vated IS NULL;