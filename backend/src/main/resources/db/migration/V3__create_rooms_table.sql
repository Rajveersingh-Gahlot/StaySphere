
CREATE TABLE rooms (
    id UUID PRIMARY KEY,

    property_id UUID NOT NULL,

    room_number VARCHAR(30) NOT NULL,
    description TEXT,
    floor_number INTEGER NOT NULL,

    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT fk_rooms_property
        FOREIGN KEY (property_id)
        REFERENCES pg_properties(id),

    CONSTRAINT uk_rooms_property_room_number
        UNIQUE (property_id, room_number)
);
