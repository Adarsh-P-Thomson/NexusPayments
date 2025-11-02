package com.apiserver.apinexus.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class VerifySchema {
    
    private static final String POSTGRES_URL = "jdbc:postgresql://localhost:5432/nexuspay";
    private static final String POSTGRES_USER = "postgres";
    private static final String POSTGRES_PASSWORD = "root";
    
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(POSTGRES_URL, POSTGRES_USER, POSTGRES_PASSWORD)) {
            
            // Show analytics_login table
            System.out.println("\n================================================================================");
            System.out.println("ANALYTICS_LOGIN TABLE");
            System.out.println("================================================================================");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id, username, email, is_active FROM analytics_login")) {
                System.out.printf("%-5s %-20s %-30s %-10s%n", "ID", "Username", "Email", "Active");
                System.out.println("--------------------------------------------------------------------------------");
                while (rs.next()) {
                    System.out.printf("%-5d %-20s %-30s %-10s%n",
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getBoolean("is_active") ? "Yes" : "No"
                    );
                }
            }
            
            // Show user_details table
            System.out.println("\n================================================================================");
            System.out.println("USER_DETAILS TABLE");
            System.out.println("================================================================================");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id, username, email, full_name, is_premium, is_active FROM user_details")) {
                System.out.printf("%-5s %-20s %-30s %-20s %-10s %-10s%n", 
                    "ID", "Username", "Email", "Full Name", "Premium", "Active");
                System.out.println("--------------------------------------------------------------------------------");
                while (rs.next()) {
                    System.out.printf("%-5d %-20s %-30s %-20s %-10s %-10s%n",
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("full_name"),
                        rs.getBoolean("is_premium") ? "Yes" : "No",
                        rs.getBoolean("is_active") ? "Yes" : "No"
                    );
                }
            }
            
            // Show market_items table
            System.out.println("\n================================================================================");
            System.out.println("MARKET_ITEMS TABLE");
            System.out.println("================================================================================");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id, item_name, category, normal_price, premium_price, stock_quantity FROM market_items")) {
                System.out.printf("%-5s %-35s %-20s %-12s %-12s %-10s%n", 
                    "ID", "Item Name", "Category", "Normal $", "Premium $", "Stock");
                System.out.println("--------------------------------------------------------------------------------");
                while (rs.next()) {
                    System.out.printf("%-5d %-35s %-20s $%-11.2f $%-11.2f %-10d%n",
                        rs.getInt("id"),
                        rs.getString("item_name"),
                        rs.getString("category"),
                        rs.getDouble("normal_price"),
                        rs.getDouble("premium_price"),
                        rs.getInt("stock_quantity")
                    );
                }
            }
            
            System.out.println("\n================================================================================");
            System.out.println("✓ All tables verified successfully!");
            System.out.println("================================================================================\n");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
