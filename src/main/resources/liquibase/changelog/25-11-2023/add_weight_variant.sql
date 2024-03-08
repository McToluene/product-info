ALTER TABLE variants_awaiting_approval
    ADD COLUMN IF NOT EXISTS weight DECIMAL(18,2);


ALTER TABLE product_variants
    ADD COLUMN IF NOT EXISTS weight DECIMAL(18,2);