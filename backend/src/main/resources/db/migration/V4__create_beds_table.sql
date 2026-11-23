CREATE TABLE beds (
    id UUID PRIMARY KEY,

    room_id UUID NOT NULL,

    bed_number VARCHAR(30) NOT NULL,
    monthly_rent NUMERIC(12, 2) NOT NULL,

    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT fk_beds_room
        FOREIGN KEY (room_id)
        REFERENCES rooms(id),

    CONSTRAINT uk_beds_room_number
        UNIQUE (room_id, bed_number),

    CONSTRAINT chk_beds_monthly_rent
        CHECK (monthly_rent > 0)
);
