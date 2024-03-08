INSERT INTO measuring_unit (id, public_id, name, abbreviation, status, created_date, created_by, last_modified_date, last_modified_by)
VALUES
    ('15225e7b-43b3-42b9-96c1-e1d5ba6fc37c', '6c38401c-4dbb-41e4-b7dd-7d47b5324a76', 'KG', 'KG', 'ACTIVE', NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
    ('6f291fd0-a962-4434-88e3-9d5d1fde5128', 'eec2948a-3a0c-4e8d-b35d-faf644448ea0', 'SHEET', 'SHEET', 'ACTIVE', NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
    ('0a82b401-791f-4504-972a-87875b871566', 'ea20ca9a-f8b9-4ac6-804d-6cabd58091d6', 'NUMBER', 'NUMBER', 'ACTIVE', NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
    ('c6f85384-6b7f-49b6-a5cc-b9affc7d412c', 'e39a4289-c18c-451f-aaad-1ed18fbe35d0', 'CM²', 'CM²', 'ACTIVE', NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
    ('f9cb5166-f986-499e-b199-84efd44654e9', '5e8e13c0-42e8-41d1-a2c1-cbeebcc880fb', 'LITRE', 'LITRE', 'ACTIVE', NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
    ('f954221c-65d0-46b1-9fc6-c3a451a0664f', 'd6adb56d-b478-4a13-bd39-af6fbe4547b8', 'ML', 'ML', 'ACTIVE', NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
    ('430fdf9c-1617-4d26-b47d-11812aae0f48', 'd7610ca1-f54b-465a-82d9-203e58afd2d2', 'GRAM', 'GRAM', 'ACTIVE', NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
    ('78f5f21a-d8b6-456e-8363-fbe8e1e7266c', 'c97457c6-ce0f-4747-a189-3c5e1b4eb401', 'REAM', 'REAM', 'ACTIVE', NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
    ('001040a4-5631-443f-9b53-866aad91d97b', '198220da-6b45-49cb-a98a-c9a7d88ea028', 'CM', 'CM', 'ACTIVE', NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
    ('e8381f9c-fa10-4d62-9395-38be5929f647', 'e62ad9c7-5837-483e-a65c-3760ac2cf1ae', 'INCH', 'INCH', 'ACTIVE', NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
    ('89009400-60db-4200-8bd3-615d8da22362', '99f3f999-18e2-4ee3-8f75-010fd0750862', 'METER', 'METER', 'ACTIVE', NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
    ('e6477f76-2439-4a27-955d-0c0d387a31fe', 'f1de1c74-96dd-4d6d-a9aa-1b052d1fae09', 'UNIT', 'UNIT', 'ACTIVE', NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
    ('b071b2fb-aa05-4777-97be-b1b202cdf4db', 'c9e67c98-9ec1-4362-b6d5-427f27c746ef', 'TONNE', 'TONNE', 'ACTIVE', NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
    ('efd59ac9-fde4-43a5-8551-eb59180c0315', 'dc35caba-55f8-4774-966c-c8206ec77401', 'FEET', 'FEET', 'ACTIVE', NOW(), 'SYSTEM', NOW(), 'SYSTEM');

UPDATE products
 SET measuring_unit_id = '15225e7b-43b3-42b9-96c1-e1d5ba6fc37c' ;

DELETE FROM measuring_unit
    WHERE id not in
    ('15225e7b-43b3-42b9-96c1-e1d5ba6fc37c', '6f291fd0-a962-4434-88e3-9d5d1fde5128',
    '0a82b401-791f-4504-972a-87875b871566', 'c6f85384-6b7f-49b6-a5cc-b9affc7d412c',
    'f9cb5166-f986-499e-b199-84efd44654e9', 'f954221c-65d0-46b1-9fc6-c3a451a0664f',
    '430fdf9c-1617-4d26-b47d-11812aae0f48', '78f5f21a-d8b6-456e-8363-fbe8e1e7266c',
    '001040a4-5631-443f-9b53-866aad91d97b', 'e8381f9c-fa10-4d62-9395-38be5929f647',
    '89009400-60db-4200-8bd3-615d8da22362', 'e6477f76-2439-4a27-955d-0c0d387a31fe',
    'b071b2fb-aa05-4777-97be-b1b202cdf4db', 'efd59ac9-fde4-43a5-8551-eb59180c0315');