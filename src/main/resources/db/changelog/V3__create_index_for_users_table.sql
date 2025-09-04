--liquibase formatted sql

--changeset create_index_for_users_table:1
CREATE INDEX ind_users_name_surname ON users(name, surname);
