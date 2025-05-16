CREATE TABLE employee (
    employee_id BIGSERIAL PRIMARY KEY,
    login       VARCHAR(30) NOT NULL,
    name        VARCHAR(100),
    position    VARCHAR(50)
);