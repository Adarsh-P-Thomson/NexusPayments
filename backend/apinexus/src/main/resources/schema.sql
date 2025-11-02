-- ================================================================================
-- NexusPay Database Schema
-- Database: nexuspay
-- ================================================================================

-- Table: analytics_login
-- Purpose: Store login credentials for analytics site (unencrypted for project)
CREATE TABLE IF NOT EXISTS analytics_login (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: user_details
-- Purpose: Store user information and login passwords
CREATE TABLE IF NOT EXISTS user_details (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(200),
    phone_number VARCHAR(20),
    is_premium BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: market_items
-- Purpose: Store market items with dual pricing for premium and normal users
CREATE TABLE IF NOT EXISTS market_items (
    id BIGSERIAL PRIMARY KEY,
    item_name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    normal_price DECIMAL(10, 2) NOT NULL,
    premium_price DECIMAL(10, 2) NOT NULL,
    stock_quantity INTEGER DEFAULT 0,
    image_url VARCHAR(500),
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_analytics_login_username ON analytics_login(username);
CREATE INDEX IF NOT EXISTS idx_analytics_login_email ON analytics_login(email);
CREATE INDEX IF NOT EXISTS idx_user_details_username ON user_details(username);
CREATE INDEX IF NOT EXISTS idx_user_details_email ON user_details(email);
CREATE INDEX IF NOT EXISTS idx_user_details_is_premium ON user_details(is_premium);
CREATE INDEX IF NOT EXISTS idx_market_items_category ON market_items(category);
CREATE INDEX IF NOT EXISTS idx_market_items_is_available ON market_items(is_available);

-- Insert sample data for analytics login
INSERT INTO analytics_login (username, password, email) VALUES
('admin', 'admin123', 'admin@nexuspay.com'),
('analyst', 'analyst123', 'analyst@nexuspay.com')
ON CONFLICT (username) DO NOTHING;

-- Insert sample data for user details
INSERT INTO user_details (username, password, email, full_name, phone_number, is_premium) VALUES
('john_doe', 'password123', 'john.doe@example.com', 'John Doe', '+1234567890', FALSE),
('jane_smith', 'password123', 'jane.smith@example.com', 'Jane Smith', '+1234567891', TRUE),
('premium_user', 'premium123', 'premium@example.com', 'Premium User', '+1234567892', TRUE),
('normal_user', 'normal123', 'normal@example.com', 'Normal User', '+1234567893', FALSE)
ON CONFLICT (username) DO NOTHING;

-- Insert sample data for market items
INSERT INTO market_items (item_name, description, category, normal_price, premium_price, stock_quantity, is_available) VALUES
('Premium Coffee Beans', 'High-quality Arabica coffee beans', 'Food & Beverages', 24.99, 19.99, 100, TRUE),
('Wireless Earbuds', 'Bluetooth 5.0 wireless earbuds with noise cancellation', 'Electronics', 79.99, 59.99, 50, TRUE),
('Yoga Mat', 'Non-slip eco-friendly yoga mat', 'Sports & Fitness', 34.99, 27.99, 75, TRUE),
('Organic Green Tea', 'Premium organic green tea leaves', 'Food & Beverages', 15.99, 12.99, 150, TRUE),
('Smart Watch', 'Fitness tracker with heart rate monitor', 'Electronics', 199.99, 149.99, 30, TRUE),
('Protein Powder', 'Whey protein isolate - Chocolate flavor', 'Health & Wellness', 49.99, 39.99, 60, TRUE),
('Running Shoes', 'Lightweight running shoes with cushioned sole', 'Sports & Fitness', 89.99, 69.99, 40, TRUE),
('Notebook Set', 'Premium leather-bound notebook set of 3', 'Stationery', 29.99, 24.99, 80, TRUE),
('Water Bottle', 'Insulated stainless steel water bottle 1L', 'Sports & Fitness', 19.99, 15.99, 120, TRUE),
('Desk Lamp', 'LED desk lamp with adjustable brightness', 'Home & Office', 44.99, 34.99, 45, TRUE)
ON CONFLICT DO NOTHING;

-- ================================================================================
-- Schema creation complete
-- ================================================================================
