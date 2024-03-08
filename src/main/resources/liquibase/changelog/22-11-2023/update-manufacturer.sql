UPDATE manufacturers
SET status = 'ACTIVE'
WHERE status IS NULL;