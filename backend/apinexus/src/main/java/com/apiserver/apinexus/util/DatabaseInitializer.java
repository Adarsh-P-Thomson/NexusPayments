package com.apiserver.apinexus.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseInitializer {
    
    private static final String POSTGRES_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String POSTGRES_USER = "postgres";
    private static final String POSTGRES_PASSWORD = "root";
    private static final String DATABASE_NAME = "nexuspay";
    
    public static void main(String[] args) {
        createDatabaseIfNotExists();
    }
    
    public static void createDatabaseIfNotExists() {
        System.out.println("=".repeat(80));
        System.out.println("DATABASE INITIALIZATION");
        System.out.println("=".repeat(80));
        
        try (Connection conn = DriverManager.getConnection(POSTGRES_URL, POSTGRES_USER, POSTGRES_PASSWORD)) {
            System.out.println("✓ Connected to PostgreSQL server");
            
            // Check if database exists
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(
                    "SELECT 1 FROM pg_database WHERE datname = '" + DATABASE_NAME + "'");
                
                if (rs.next()) {
                    System.out.println("✓ Database '" + DATABASE_NAME + "' already exists");
                } else {
                    // Create database
                    stmt.executeUpdate("CREATE DATABASE " + DATABASE_NAME);
                    System.out.println("✓ Database '" + DATABASE_NAME + "' created successfully!");
                }
            }
            
            System.out.println("=".repeat(80));
            System.out.println("✓ Database initialization completed!");
            System.out.println("You can now run the Spring Boot application.");
            System.out.println("=".repeat(80));
            
        } catch (Exception e) {
            System.err.println("✗ Failed to initialize database");
            System.err.println("Error: " + e.getMessage());
            System.err.println("\nPlease create the database manually using pgAdmin or psql:");
            System.err.println("  CREATE DATABASE nexuspay;");
            e.printStackTrace();
        }
    }
}
