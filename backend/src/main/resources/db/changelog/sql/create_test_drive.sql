CREATE TABLE test_drive (
    test_drive_id BIGSERIAL PRIMARY KEY,
    car_id        BIGINT NOT NULL,
    employee_id   BIGINT NOT NULL,
    client_id     BIGINT NOT NULL,
    drive_date    TIMESTAMP,
    price         NUMERIC(15, 2),
    status        VARCHAR(20),
    hours         INTEGER
);