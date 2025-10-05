-- PostgreSQL Initialization Script for NexusPay
-- This script runs automatically when the Docker container is first created

-- Create users table (if not exists - JPA will create it)
-- This is just for reference, JPA handles table creation

-- Insert sample users
INSERT INTO users (email, name, created_at, updated_at) 
VALUES 
    ('demo@nexuspay.com', 'Demo User', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('john.doe@example.com', 'John Doe', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('jane.smith@example.com', 'Jane Smith', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;

-- Insert sample subscription plans
INSERT INTO subscription_plans (name, description, monthly_price, yearly_price, features, active, created_at, updated_at)
VALUES 
    ('Basic Plan', 'Perfect for individuals and small teams', 9.99, 99.99, 'Up to 5 users, 10GB storage, Email support', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Professional Plan', 'Ideal for growing businesses', 29.99, 299.99, 'Up to 50 users, 100GB storage, Priority support, API access', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Enterprise Plan', 'For large organizations', 99.99, 999.99, 'Unlimited users, 1TB storage, 24/7 support, Custom integrations', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Create card_details table for storing user card information
CREATE TABLE IF NOT EXISTS card_details (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    card_number_last4 VARCHAR(4) NOT NULL,
    card_holder_name VARCHAR(255) NOT NULL,
    card_type VARCHAR(50),
    expiry_month INTEGER NOT NULL,
    expiry_year INTEGER NOT NULL,
    is_default BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_card FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create index on user_id for faster lookups
CREATE INDEX IF NOT EXISTS idx_card_details_user_id ON card_details(user_id);

-- Insert sample card details
INSERT INTO card_details (user_id, card_number_last4, card_holder_name, card_type, expiry_month, expiry_year, is_default, created_at, updated_at)
VALUES 
    (1, '4242', 'Demo User', 'VISA', 12, 2025, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, '5555', 'John Doe', 'MASTERCARD', 6, 2026, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, '3782', 'Jane Smith', 'AMEX', 9, 2027, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;
