--liquibase formatted sql

--changeset create_card_info_table:1
CREATE TABLE card_info
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    number          VARCHAR(16)  NOT NULL UNIQUE,
    holder          VARCHAR(128) NOT NULL,
    expiration_date DATE         NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
