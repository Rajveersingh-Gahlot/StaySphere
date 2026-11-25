CREATE TABLE property_images (
    id UUID PRIMARY KEY,

    property_id UUID NOT NULL,

    image_url TEXT NOT NULL,
    public_id TEXT NOT NULL,
    is_cover BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,

    CONSTRAINT fk_property_images_property
        FOREIGN KEY (property_id)
        REFERENCES pg_properties(id),

    CONSTRAINT uk_property_images_public_id
        UNIQUE (public_id)
);

CREATE INDEX idx_property_images_property_id
    ON property_images(property_id);

CREATE UNIQUE INDEX uk_property_images_cover
    ON property_images(property_id)
    WHERE is_cover = TRUE;