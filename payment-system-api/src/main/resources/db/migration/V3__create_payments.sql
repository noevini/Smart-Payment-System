create table payments (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    business_id BIGINT NOT NULL,
    direction VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    currency CHAR(3) NOT NULL,
    description VARCHAR(255),
    due_date TIMESTAMP NOT NULL,
    paid_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

ALTER TABLE payments
ADD CONSTRAINT fk_payments_business
FOREIGN KEY (business_id)
REFERENCES businesses(id);