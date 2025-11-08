CREATE TABLE IF NOT EXISTS device (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    description VARCHAR(500),
    max_consumption NUMERIC(10, 2) NOT NULL CHECK (max_consumption > 0),
    user_id UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_device_user_id ON device(user_id);
