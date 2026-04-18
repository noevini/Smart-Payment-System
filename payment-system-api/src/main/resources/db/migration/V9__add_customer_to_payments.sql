ALTER TABLE payments
    ADD COLUMN customer_id bigint,
    ADD CONSTRAINT fk_payments_customer
        FOREIGN KEY (customer_id) REFERENCES customers(id)
            ON DELETE SET NULL;
