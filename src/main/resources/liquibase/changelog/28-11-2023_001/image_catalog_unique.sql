DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'unique_image_catalog_name'
    ) THEN
ALTER TABLE image_catalog DROP CONSTRAINT unique_image_catalog_name;
END IF;
END
$$;