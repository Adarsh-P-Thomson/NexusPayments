-- PostgreSQL Initialization Script for NexusPay
-- This script runs automatically when the Docker container is first created
-- It creates the necessary tables and seeds initial data.

-- Ensure the script is idempotent by using 'CREATE TABLE IF NOT EXISTS'

-- Table for Users
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- Table for Card Details
CREATE TABLE IF NOT EXISTS card_details (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id),
    cardholder_name VARCHAR(255),
    last_4_digits VARCHAR(4) NOT NULL,
    card_type VARCHAR(50),
    expiry_month INTEGER NOT NULL,
    expiry_year INTEGER NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- Table for Subscription Plans
CREATE TABLE IF NOT EXISTS subscription_plans (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    monthly_price NUMERIC(10, 2) NOT NULL,
    yearly_price NUMERIC(10, 2) NOT NULL,
    features TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- Table for User Subscriptions
CREATE TABLE IF NOT EXISTS user_subscriptions (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id),
    plan_id INTEGER NOT NULL REFERENCES subscription_plans(id),
    status VARCHAR(50) NOT NULL, -- e.g., 'ACTIVE', 'CANCELLED', 'EXPIRED'
    billing_cycle VARCHAR(50) NOT NULL, -- e.g., 'MONTHLY', 'YEARLY'
    start_date TIMESTAMPTZ NOT NULL,
    end_date TIMESTAMPTZ,
    next_billing_date TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- Table for Bills
CREATE TABLE IF NOT EXISTS bills (
    id SERIAL PRIMARY KEY,
    user_subscription_id INTEGER NOT NULL REFERENCES user_subscriptions(id),
    amount NUMERIC(10, 2) NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL, -- e.g., 'PENDING', 'PAID', 'FAILED'
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- Insert initial data for subscription plans
-- We use ON CONFLICT DO NOTHING to prevent errors on subsequent runs
INSERT INTO subscription_plans (name, monthly_price, yearly_price, features, active) VALUES
('Basic', 9.99, 99.99, '["Feature A", "Feature B"]', TRUE)
ON CONFLICT (name) DO NOTHING;

INSERT INTO subscription_plans (name, monthly_price, yearly_price, features, active) VALUES
('Professional', 29.99, 299.99, '["Feature A", "Feature B", "Feature C"]', TRUE)
ON CONFLICT (name) DO NOTHING;

INSERT INTO subscription_plans (name, monthly_price, yearly_price, features, active) VALUES
('Enterprise', 99.99, 999.99, '["All Features", "Dedicated Support"]', TRUE)
ON CONFLICT (name) DO NOTHING;


-- Log completion
DO $$
BEGIN
   RAISE NOTICE 'PostgreSQL initialization script executed successfully.';
   RAISE NOTICE 'Tables created: users, card_details, subscription_plans, user_subscriptions, bills.';
   RAISE NOTICE 'Initial subscription plans have been seeded.';
END;
$$;
