# --- !Ups

CREATE TABLE statuses
(
    id   SERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(50)        NOT NULL
);

CREATE TABLE records
(
    id        SERIAL PRIMARY KEY NOT NULL,
    todo_item VARCHAR(200)       NOT NULL,
    status_id BIGINT             NOT NULL
);

ALTER TABLE records
    ADD CONSTRAINT fk_records_1 FOREIGN KEY (status_id) REFERENCES statuses (id);

# --- !Downs
