CREATE TABLE IF not exists users(
    id SERIAL NOT NULL PRIMARY KEY,
    username varchar(50) NOT NULL UNIQUE,
    password varchar(50) NOT NULL,
    userRole  varchar(20) CHECK (userRole IN ('USER', 'ADMIN')) NOT NULL,
    firstName varChar(50) NOT NULL,
    lastName varChar(50) NOT NULL,
    organisationName varChar(50) NOT NULL,
    accessStatus  varchar(20) CHECK (accessStatus IN ('ALLOWED', 'BLOCKED')) NOT NULL );

INSERT INTO users (username, password, userRole, firstName, lastName, organisationName, accessStatus)
    VALUES ('ADMIN', 'passwordAdmin', 'ADMIN', 'admin', 'admin', 'ADMIN', 'ALLOWED') ON CONFLICT (id) DO NOTHING;
