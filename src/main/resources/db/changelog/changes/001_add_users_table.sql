--liquibase formatted sql
--changeset nunopinho:001_add_users_table

CREATE TABLE users
(
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL
);

-- rollback DROP TABLE users;
