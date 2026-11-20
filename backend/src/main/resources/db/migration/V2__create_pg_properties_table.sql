CREATE TABLE pg_properties (
    id UUID PRIMARY KEY,

    owner_id UUID NOT NULL,

    name VARCHAR(150) NOT NULL,
    description TEXT,

    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    pincode VARCHAR(10) NOT NULL,

    pg_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,

    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT fk_pg_properties_owner
        FOREIGN KEY (owner_id)
        REFERENCES users(id)
);

CREATE INDEX idx_pg_properties_owner_id
    ON pg_properties(owner_id);
