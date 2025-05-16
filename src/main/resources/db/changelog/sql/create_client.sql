CREATE TABLE client (
    client_id          BIGSERIAL PRIMARY KEY,
    login              VARCHAR(30) NOT NULL,
    name               VARCHAR(100),
    email              VARCHAR(100),
    birthday           DATE,
    address            VARCHAR(255),
    passport           VARCHAR(50),
    driver_license     VARCHAR(50),
    first_license_date DATE
);