--liquibase formatted sql

--changeset MrGriv:2

ALTER TABLE registration ADD COLUMN state VARCHAR(20);
ALTER TABLE registration ADD COLUMN created_date_time TIMESTAMP WITHOUT TIME ZONE;