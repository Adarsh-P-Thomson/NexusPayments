-- Update subscription_plans table with new columns for offer management

-- Add new columns
ALTER TABLE subscription_plans ADD COLUMN IF NOT EXISTS is_default BOOLEAN DEFAULT false;
ALTER TABLE subscription_plans ADD COLUMN IF NOT EXISTS plan_type VARCHAR(50);
ALTER TABLE subscription_plans ADD COLUMN IF NOT EXISTS discount_percentage INTEGER DEFAULT 0;
ALTER TABLE subscription_plans ADD COLUMN IF NOT EXISTS offer_valid_until TIMESTAMP;
ALTER TABLE subscription_plans ADD COLUMN IF NOT EXISTS max_users INTEGER;
ALTER TABLE subscription_plans ADD COLUMN IF NOT EXISTS max_bills_per_month INTEGER;
ALTER TABLE subscription_plans ADD COLUMN IF NOT EXISTS priority_support BOOLEAN DEFAULT false;
ALTER TABLE subscription_plans ADD COLUMN IF NOT EXISTS custom_branding BOOLEAN DEFAULT false;

-- Update features column to TEXT if not already
ALTER TABLE subscription_plans ALTER COLUMN features TYPE TEXT;

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_subscription_plans_is_default ON subscription_plans(is_default);
CREATE INDEX IF NOT EXISTS idx_subscription_plans_plan_type ON subscription_plans(plan_type);
CREATE INDEX IF NOT EXISTS idx_subscription_plans_active ON subscription_plans(active);
CREATE INDEX IF NOT EXISTS idx_subscription_plans_offer_valid ON subscription_plans(offer_valid_until);

-- Display confirmation
SELECT 'Subscription plans table updated successfully!' as status;
