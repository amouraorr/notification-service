-- V1: create notifications table

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS notifications (
	id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
	parcel_id BIGINT,
	resident_name VARCHAR(255) NOT NULL,
	apartment VARCHAR(50),
	contact VARCHAR(255),
	channel VARCHAR(50) NOT NULL DEFAULT 'PUSH',-- e.g., EMAIL, SMS, PUSH
	message TEXT,
	description TEXT,
	status VARCHAR(50) NOT NULL DEFAULT 'PENDING',-- PENDING, SENT, FAILED
	result_detail TEXT,
	sent_at TIMESTAMPTZ,
	created_at TIMESTAMPTZ DEFAULT now(),
	updated_at TIMESTAMPTZ DEFAULT now(),
	acknowledged BOOLEAN DEFAULT FALSE
);
CREATE INDEX IF NOT EXISTS idx_notifications_parcel_id ON notifications (parcel_id);
-- Índice para consultas por parcel_id