ALTER TABLE product_categories
    ADD COLUMN IF NOT EXISTS country_id uuid;