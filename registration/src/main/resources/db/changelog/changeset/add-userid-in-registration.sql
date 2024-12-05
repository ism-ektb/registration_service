--liquibase formatted sql

--changeset ism:1

ALTER TABLE registration ADD COLUMN user_id bigint;
;