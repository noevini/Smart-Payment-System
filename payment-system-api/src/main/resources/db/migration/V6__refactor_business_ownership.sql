ALTER TABLE users
    ADD COLUMN business_id BIGINT NULL;

ALTER TABLE users
    ADD CONSTRAINT fk_users_business
        FOREIGN KEY (business_id)
            REFERENCES businesses(id);

CREATE INDEX idx_users_business_id
    ON users(business_id);

CREATE TABLE business_owners (
        business_id BIGINT NOT NULL,
        owner_id    BIGINT NOT NULL,

        PRIMARY KEY (business_id, owner_id),

        CONSTRAINT fk_business_owners_business
        FOREIGN KEY (business_id)
        REFERENCES businesses(id)
        ON DELETE CASCADE,

        CONSTRAINT fk_business_owners_owner
        FOREIGN KEY (owner_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_business_owners_owner_id
    ON business_owners(owner_id);

CREATE INDEX idx_business_owners_business_id
    ON business_owners(business_id);

INSERT INTO business_owners (business_id, owner_id)
SELECT id, owner_id
FROM businesses;
DROP INDEX IF EXISTS uq_businesses_owner_name;
DROP INDEX IF EXISTS idx_businesses_owner_id;

ALTER TABLE businesses
    DROP COLUMN owner_id;
