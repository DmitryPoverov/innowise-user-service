--liquibase formatted sql

--changeset create_user_table:1
CREATE TABLE users
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(64) NOT NULL,
    surname    VARCHAR(64) NOT NULL,
    birth_date DATE NOT NULL,
    email      VARCHAR(64) NOT NULL UNIQUE
);
