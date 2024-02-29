CREATE TABLE IF not exists organisations(
    id SERIAL NOT NULL PRIMARY KEY,
    name varchar(50) NOT NULL UNIQUE);

/* The first userRole id in this entity is 1 as the admin is created in the auction_user script which runs before this one */
INSERT INTO organisations (name)
    VALUES ('ADMIN') ON CONFLICT (id) DO NOTHING;

