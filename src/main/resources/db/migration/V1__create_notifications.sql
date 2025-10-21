-- V1: create notifications table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    parcel_id BIGINT NOT NULL,
    recipient_name VARCHAR(255),
    apartment VARCHAR(50),
    message TEXT,
    channel VARCHAR(50) NOT NULL, -- e.g., EMAIL, SMS, PUSH
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, SENT, FAILED
    sent_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Índice para consultas por parcel_id
CREATE INDEX IF NOT EXISTS idx_notifications_parcel_id ON notifications (parcel_id);