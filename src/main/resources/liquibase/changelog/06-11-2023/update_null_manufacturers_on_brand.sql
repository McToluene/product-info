UPDATE brands
SET manufacturer_id = (
        SELECT manufacturer_id
        FROM products p
        WHERE p.brand_id = brands.id
        ORDER BY p.manufacturer_id DESC
        LIMIT 1
    )
WHERE manufacturer_id IS NULL;