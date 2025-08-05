--liquibase formatted sql

--changeset create_index_for_card_info_table:1
CREATE INDEX ind_card_info_holder ON card_info(holder);
