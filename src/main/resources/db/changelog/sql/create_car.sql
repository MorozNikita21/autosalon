CREATE TABLE car (
    car_id       BIGSERIAL PRIMARY KEY,
    equipment_id BIGINT NOT NULL,
    vin          VARCHAR(17) UNIQUE
);