ALTER TABLE employee
    ADD CONSTRAINT fk_employee_account
        FOREIGN KEY (login) REFERENCES account (login);

ALTER TABLE client
    ADD CONSTRAINT fk_client_account
        FOREIGN KEY (login) REFERENCES account (login);

ALTER TABLE equipment
    ADD CONSTRAINT fk_equipment_model
        FOREIGN KEY (model_id) REFERENCES model (model_id);

ALTER TABLE car
    ADD CONSTRAINT fk_car_equipment
        FOREIGN KEY (equipment_id) REFERENCES equipment (equipment_id);

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_client
        FOREIGN KEY (client_id) REFERENCES client (client_id);

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_car
        FOREIGN KEY (car_id) REFERENCES car (car_id);

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_employee
        FOREIGN KEY (employee_id) REFERENCES employee (employee_id);

ALTER TABLE test_drive
    ADD CONSTRAINT fk_test_drive_car
        FOREIGN KEY (car_id) REFERENCES car (car_id);

ALTER TABLE test_drive
    ADD CONSTRAINT fk_test_drive_employee
        FOREIGN KEY (employee_id) REFERENCES employee (employee_id);

ALTER TABLE test_drive
    ADD CONSTRAINT fk_test_drive_client
        FOREIGN KEY (client_id) REFERENCES client (client_id);

ALTER TABLE score
    ADD CONSTRAINT fk_score_orders
        FOREIGN KEY (order_id) REFERENCES orders (order_id);
