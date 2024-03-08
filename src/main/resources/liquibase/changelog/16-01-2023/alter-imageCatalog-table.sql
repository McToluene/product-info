ALTER TABLE image_catalog
    ADD image_catalog_image_name character varying(255);

ALTER TABLE image_catalog
    ADD CONSTRAINT unique_image_catalog_image_name UNIQUE(image_catalog_image_name);


