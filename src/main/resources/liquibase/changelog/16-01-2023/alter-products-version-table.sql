
ALTER TABLE products_version  DROP COLUMN created_date;
ALTER TABLE products_version  DROP COLUMN created_by;
ALTER TABLE products_version  DROP COLUMN last_modified_by;
    ALTER TABLE products_version
        ADD status character varying(30);