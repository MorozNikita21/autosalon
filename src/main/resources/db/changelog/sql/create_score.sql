CREATE TABLE score (
    score_id   BIGSERIAL PRIMARY KEY,
    order_id   BIGINT NOT NULL,
    sum        NUMERIC(15, 2),
    status     VARCHAR(20),
    score_date TIMESTAMP
);