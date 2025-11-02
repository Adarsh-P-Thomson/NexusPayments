package com.apiserver.apinexus.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDateTime;

/**
 * Utility to populate subscription plans with default and offer plans
 */
public class SubscriptionPlanInitializer {
    
    private static final String POSTGRES_URL = "jdbc:postgresql://localhost:5432/nexuspay";
    private static final String POSTGRES_USER = "postgres";
    private static final String POSTGRES_PASSWORD = "root";
    
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("SUBSCRIPTION PLANS INITIALIZATION");
        System.out.println("================================================================================");
        
        try (Connection conn = DriverManager.getConnection(POSTGRES_URL, POSTGRES_USER, POSTGRES_PASSWORD)) {
            System.out.println("✓ Connected to PostgreSQL");
            
            try (Statement stmt = conn.createStatement()) {
                
                // Clear existing plans
                int deleted = stmt.executeUpdate("DELETE FROM subscription_plans");
                System.out.println("✓ Cleared existing plans: " + deleted + " rows deleted");
                
                // Insert Default Monthly Plan
                stmt.executeUpdate(
                    "INSERT INTO subscription_plans (name, description, monthly_price, yearly_price, features, " +
                    "active, is_default, plan_type, max_users, max_bills_per_month, priority_support, custom_branding) VALUES (" +
                    "'Basic Monthly', " +
                    "'Standard monthly subscription with essential features', " +
                    "9.99, " +
                    "99.99, " +
                    "'[\"Up to 5 users\", \"100 bills per month\", \"Email support\", \"Basic analytics\", \"Mobile app access\"]', " +
                    "true, " +
                    "true, " +
                    "'BASIC', " +
                    "5, " +
                    "100, " +
                    "false, " +
                    "false)"
                );
                System.out.println("✓ Created: Basic Monthly Plan");
                
                // Insert Default Yearly Plan (with discount)
                stmt.executeUpdate(
                    "INSERT INTO subscription_plans (name, description, monthly_price, yearly_price, features, " +
                    "active, is_default, plan_type, max_users, max_bills_per_month, priority_support, custom_branding, discount_percentage) VALUES (" +
                    "'Basic Yearly', " +
                    "'Annual subscription - Save 17% compared to monthly!', " +
                    "8.33, " +
                    "99.99, " +
                    "'[\"Up to 5 users\", \"100 bills per month\", \"Email support\", \"Basic analytics\", \"Mobile app access\", \"Save $20 per year\"]', " +
                    "true, " +
                    "true, " +
                    "'BASIC', " +
                    "5, " +
                    "100, " +
                    "false, " +
                    "false, " +
                    "17)"
                );
                System.out.println("✓ Created: Basic Yearly Plan");
                
                // Insert Premium Monthly Plan
                stmt.executeUpdate(
                    "INSERT INTO subscription_plans (name, description, monthly_price, yearly_price, features, " +
                    "active, is_default, plan_type, max_users, max_bills_per_month, priority_support, custom_branding) VALUES (" +
                    "'Premium Monthly', " +
                    "'Premium features for growing businesses', " +
                    "29.99, " +
                    "299.99, " +
                    "'[\"Up to 20 users\", \"Unlimited bills\", \"Priority support\", \"Advanced analytics\", \"API access\", \"Custom reports\", \"Mobile app access\"]', " +
                    "true, " +
                    "true, " +
                    "'PREMIUM', " +
                    "20, " +
                    "-1, " +
                    "true, " +
                    "false)"
                );
                System.out.println("✓ Created: Premium Monthly Plan");
                
                // Insert Premium Yearly Plan (with discount)
                stmt.executeUpdate(
                    "INSERT INTO subscription_plans (name, description, monthly_price, yearly_price, features, " +
                    "active, is_default, plan_type, max_users, max_bills_per_month, priority_support, custom_branding, discount_percentage) VALUES (" +
                    "'Premium Yearly', " +
                    "'Annual premium subscription - Save 20%!', " +
                    "24.99, " +
                    "299.99, " +
                    "'[\"Up to 20 users\", \"Unlimited bills\", \"Priority support\", \"Advanced analytics\", \"API access\", \"Custom reports\", \"Mobile app access\", \"Save $60 per year\"]', " +
                    "true, " +
                    "true, " +
                    "'PREMIUM', " +
                    "20, " +
                    "-1, " +
                    "true, " +
                    "false, " +
                    "20)"
                );
                System.out.println("✓ Created: Premium Yearly Plan");
                
                // Insert Enterprise Plan
                stmt.executeUpdate(
                    "INSERT INTO subscription_plans (name, description, monthly_price, yearly_price, features, " +
                    "active, is_default, plan_type, max_users, max_bills_per_month, priority_support, custom_branding) VALUES (" +
                    "'Enterprise', " +
                    "'Full-featured enterprise solution with custom branding', " +
                    "99.99, " +
                    "999.99, " +
                    "'[\"Unlimited users\", \"Unlimited bills\", \"24/7 dedicated support\", \"Advanced analytics\", \"API access\", \"Custom reports\", \"Custom branding\", \"SLA guarantee\", \"On-premise deployment option\"]', " +
                    "true, " +
                    "true, " +
                    "'ENTERPRISE', " +
                    "-1, " +
                    "-1, " +
                    "true, " +
                    "true)"
                );
                System.out.println("✓ Created: Enterprise Plan");
                
                // Insert Special Offer Plan (Limited Time)
                LocalDateTime offerExpiry = LocalDateTime.now().plusDays(30);
                stmt.executeUpdate(
                    "INSERT INTO subscription_plans (name, description, monthly_price, yearly_price, features, " +
                    "active, is_default, plan_type, max_users, max_bills_per_month, priority_support, custom_branding, " +
                    "discount_percentage, offer_valid_until) VALUES (" +
                    "'Black Friday Special', " +
                    "'Limited time offer - 50% OFF Premium for 6 months!', " +
                    "14.99, " +
                    "179.94, " +
                    "'[\"Up to 20 users\", \"Unlimited bills\", \"Priority support\", \"Advanced analytics\", \"API access\", \"50% OFF for 6 months\", \"Mobile app access\"]', " +
                    "true, " +
                    "false, " +
                    "'PREMIUM', " +
                    "20, " +
                    "-1, " +
                    "true, " +
                    "false, " +
                    "50, " +
                    "'" + offerExpiry + "')"
                );
                System.out.println("✓ Created: Black Friday Special Offer");
                
                // Insert Startup Offer
                stmt.executeUpdate(
                    "INSERT INTO subscription_plans (name, description, monthly_price, yearly_price, features, " +
                    "active, is_default, plan_type, max_users, max_bills_per_month, priority_support, custom_branding, " +
                    "discount_percentage, offer_valid_until) VALUES (" +
                    "'Startup Special', " +
                    "'Perfect for startups - First 3 months at 30% OFF', " +
                    "6.99, " +
                    "83.88, " +
                    "'[\"Up to 5 users\", \"100 bills per month\", \"Email support\", \"Basic analytics\", \"30% OFF for 3 months\", \"Mobile app access\"]', " +
                    "true, " +
                    "false, " +
                    "'BASIC', " +
                    "5, " +
                    "100, " +
                    "false, " +
                    "false, " +
                    "30, " +
                    "'" + LocalDateTime.now().plusDays(60) + "')"
                );
                System.out.println("✓ Created: Startup Special Offer");
                
                // Verify insertion
                var rs = stmt.executeQuery("SELECT COUNT(*) FROM subscription_plans");
                rs.next();
                int count = rs.getInt(1);
                
                System.out.println("\n================================================================================");
                System.out.println("SUMMARY");
                System.out.println("================================================================================");
                System.out.println("Total subscription plans created: " + count);
                System.out.println("  • Default plans: 5 (Basic Monthly/Yearly, Premium Monthly/Yearly, Enterprise)");
                System.out.println("  • Special offers: 2 (Black Friday, Startup)");
                
                // Display plans
                System.out.println("\n================================================================================");
                System.out.println("SUBSCRIPTION PLANS");
                System.out.println("================================================================================");
                
                rs = stmt.executeQuery(
                    "SELECT id, name, monthly_price, yearly_price, plan_type, is_default, " +
                    "discount_percentage, offer_valid_until FROM subscription_plans ORDER BY id"
                );
                
                System.out.printf("%-5s %-30s %-12s %-12s %-12s %-10s %-10s%n", 
                    "ID", "Plan Name", "Monthly", "Yearly", "Type", "Default", "Discount");
                System.out.println("--------------------------------------------------------------------------------");
                
                while (rs.next()) {
                    System.out.printf("%-5d %-30s $%-11.2f $%-11.2f %-12s %-10s %-10s%n",
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getDouble("monthly_price"),
                        rs.getDouble("yearly_price"),
                        rs.getString("plan_type"),
                        rs.getBoolean("is_default") ? "Yes" : "No",
                        rs.getInt("discount_percentage") + "%"
                    );
                }
                
                System.out.println("\n================================================================================");
                System.out.println("✓ Subscription plans initialization completed!");
                System.out.println("================================================================================");
            }
            
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
