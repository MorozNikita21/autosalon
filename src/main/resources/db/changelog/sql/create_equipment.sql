CREATE TABLE equipment (
    equipment_id BIGSERIAL PRIMARY KEY,
    model_id     BIGINT NOT NULL,
    name         VARCHAR(100),
    price        NUMERIC(15, 2),
    engine       VARCHAR(50),
    drives       VARCHAR(50),
    color        VARCHAR(50),
    salon        VARCHAR(100)
);