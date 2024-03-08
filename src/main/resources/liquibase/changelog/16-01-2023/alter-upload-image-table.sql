ALTER TABLE image
    ADD image_name character varying(255);

ALTER TABLE image
    ADD CONSTRAINT unique_image_name UNIQUE (image_name);


