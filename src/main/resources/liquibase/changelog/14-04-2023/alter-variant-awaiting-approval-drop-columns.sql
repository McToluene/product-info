ALTER table variants_awaiting_approval
DROP COLUMN completed_by;

ALTER table variants_awaiting_approval
DROP COLUMN completed_date;

ALTER table variants_awaiting_approval
ADD COLUMN completed_date TIMESTAMP;

ALTER table variants_awaiting_approval
ADD COLUMN completed_by character varying(75);