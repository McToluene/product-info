DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'unique_brand_name'
    ) THEN
ALTER TABLE brands DROP CONSTRAINT unique_brand_name;
END IF;
END
$$;
