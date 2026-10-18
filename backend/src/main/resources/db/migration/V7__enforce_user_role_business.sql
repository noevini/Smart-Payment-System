ALTER TABLE users
    ADD CONSTRAINT chk_users_role_business
        CHECK (
            (role = 'STAFF' AND business_id IS NOT NULL)
                OR
            (role = 'OWNER' AND business_id IS NULL)
            );
