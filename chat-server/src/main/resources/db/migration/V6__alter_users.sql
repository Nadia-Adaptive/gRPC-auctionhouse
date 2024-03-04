ALTER TABLE IF EXISTS users
    ADD COLUMN IF NOT EXISTS updated_at timestamp with time zone DEFAULT current_timestamp;
ALTER TABLE IF EXISTS users
    ADD COLUMN IF NOT EXISTS created_at timestamp with time zone DEFAULT current_timestamp;

ALTER TABLE IF EXISTS users
    RENAME COLUMN userRole to user_role;
ALTER TABLE IF EXISTS users
    RENAME COLUMN firstName to first_name;
ALTER TABLE IF EXISTS users
    RENAME COLUMN lastName to last_name;
ALTER TABLE IF EXISTS users
    RENAME COLUMN id to user_id;
ALTER TABLE IF EXISTS users
    RENAME COLUMN organisationName to organisation_name;
ALTER TABLE IF EXISTS users
    RENAME COLUMN accessStatus to access_status;