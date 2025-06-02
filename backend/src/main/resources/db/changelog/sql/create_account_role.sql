CREATE TABLE account_role (
    account_login VARCHAR(30) NOT NULL,
    role_id       BIGINT      NOT NULL,
    PRIMARY KEY (account_login, role_id),
    CONSTRAINT fk_account FOREIGN KEY (account_login) REFERENCES account (login),
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES role (id)
);