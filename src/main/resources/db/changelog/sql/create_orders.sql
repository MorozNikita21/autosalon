CREATE TABLE orders (
    order_id    BIGSERIAL PRIMARY KEY,
    client_id   BIGINT NOT NULL,
    car_id      BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    order_date  DATE,
    clear_price NUMERIC(15, 2),
    price       NUMERIC(15, 2)
);