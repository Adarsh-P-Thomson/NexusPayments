package com.apiserver.apinexus.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class RunMigration {
    
    private static final String POSTGRES_URL = "jdbc:postgresql://localhost:5432/nexuspay";
    private static final String POSTGRES_USER = "postgres";
    private static final String POSTGRES_PASSWORD = "root";
    
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("RUNNING DATABASE MIGRATION");
        System.out.println("================================================================================");
        
        String[] migrations = {
            "ALTER TABLE subscription_plans ADD COLUMN IF NOT EXISTS is_default BOOLEAN DEFAULT false",
            "ALTER TABLE subscription_plans ADD COLUMN IF NOT EXISTS plan_type VARCHAR(50)",
            "ALTER TABLE subscription_plans ADD COLUMN IF NOT EXISTS discount_percentage INTEGER DEFAULT 0",
            "ALTER TABLE subscription_plans ADD COLUMN IF NOT EXISTS offer_valid_until TIMESTAMP",
            "ALTER TABLE subscription_plans ADD COLUMN IF NOT EXISTS max_users INTEGER",
            "ALTER TABLE subscription_plans ADD COLUMN IF NOT EXISTS max_bills_per_month INTEGER",
            "ALTER TABLE subscription_plans ADD COLUMN IF NOT EXISTS priority_support BOOLEAN DEFAULT false",
            "ALTER TABLE subscription_plans ADD COLUMN IF NOT EXISTS custom_branding BOOLEAN DEFAULT false",
            "ALTER TABLE subscription_plans ALTER COLUMN features TYPE TEXT",
            "CREATE INDEX IF NOT EXISTS idx_subscription_plans_is_default ON subscription_plans(is_default)",
            "CREATE INDEX IF NOT EXISTS idx_subscription_plans_plan_type ON subscription_plans(plan_type)",
            "CREATE INDEX IF NOT EXISTS idx_subscription_plans_active ON subscription_plans(active)",
            "CREATE INDEX IF NOT EXISTS idx_subscription_plans_offer_valid ON subscription_plans(offer_valid_until)"
        };
        
        try (Connection conn = DriverManager.getConnection(POSTGRES_URL, POSTGRES_USER, POSTGRES_PASSWORD)) {
            System.out.println("✓ Connected to PostgreSQL");
            
            try (Statement stmt = conn.createStatement()) {
                int successCount = 0;
                for (String migration : migrations) {
                    try {
                        stmt.execute(migration);
                        successCount++;
                        String preview = migration.length() > 60 ? migration.substring(0, 60) + "..." : migration;
                        System.out.println("  ✓ " + preview);
                    } catch (Exception e) {
                        System.out.println("  ⚠ " + e.getMessage());
                    }
                }
                
                System.out.println("\n================================================================================");
                System.out.println("✓ Migration completed! (" + successCount + "/" + migrations.length + " statements)");
                System.out.println("================================================================================");
            }
            
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
