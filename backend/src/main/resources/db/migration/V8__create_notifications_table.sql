CREATE TABLE notifications(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    business_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    related_entity_type VARCHAR(50),
    related_entity_id BIGINT,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_notifications_business
    FOREIGN KEY (business_id)
    REFERENCES businesses(id)
);

CREATE INDEX idx_notifications_business_id
    ON notifications (business_id);

CREATE INDEX idx_notifications_business_read
    ON notifications (business_id, is_read);
