create table transactions(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    business_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    description VARCHAR(255),
    occurred_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_transactions_business
    FOREIGN KEY (business_id)
    REFERENCES businesses(id)
);

CREATE INDEX idx_transactions_business_id
    ON transactions (business_id);

CREATE INDEX idx_transactions_business_occurred_at
    ON transactions (business_id, occurred_at);