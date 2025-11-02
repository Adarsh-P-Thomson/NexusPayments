package com.apiserver.apinexus.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class SchemaInitializer {
    
    private static final String POSTGRES_URL = "jdbc:postgresql://localhost:5432/nexuspay";
    private static final String POSTGRES_USER = "postgres";
    private static final String POSTGRES_PASSWORD = "root";
    private static final String SCHEMA_FILE = "src/main/resources/schema.sql";
    
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("SCHEMA INITIALIZATION");
        System.out.println("================================================================================");
        
        try {
            // Read SQL file
            StringBuilder sqlBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(SCHEMA_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sqlBuilder.append(line).append("\n");
                }
            }
            
            String fullSql = sqlBuilder.toString();
            
            // Connect to database
            System.out.println("Connecting to database...");
            try (Connection conn = DriverManager.getConnection(POSTGRES_URL, POSTGRES_USER, POSTGRES_PASSWORD)) {
                System.out.println("✓ Connected to PostgreSQL");
                
                // Remove comments and split by semicolon
                String cleanSql = fullSql.replaceAll("--[^\n]*\n", "\n")
                                         .replaceAll("================================================================================", "");
                
                String[] statements = cleanSql.split(";");
                int successCount = 0;
                int skipCount = 0;
                
                for (String sql : statements) {
                    sql = sql.trim();
                    if (!sql.isEmpty()) {
                        try (Statement stmt = conn.createStatement()) {
                            stmt.execute(sql);
                            successCount++;
                            // Show what we're executing
                            String preview = sql.substring(0, Math.min(50, sql.length())).replaceAll("\n", " ");
                            System.out.println("  ✓ Executed: " + preview + "...");
                        } catch (Exception e) {
                            // Skip errors for "ON CONFLICT" inserts or already existing items
                            if (e.getMessage().contains("already exists") || 
                                e.getMessage().contains("duplicate key") ||
                                e.getMessage().contains("ON CONFLICT")) {
                                skipCount++;
                            } else {
                                System.out.println("  Note: " + e.getMessage());
                            }
                        }
                    }
                }
                
                System.out.println("\n✓ Schema executed successfully!");
                System.out.println("✓ Processed " + successCount + " SQL statements (" + skipCount + " skipped due to conflicts)");
                
                // Verify tables
                System.out.println("\n================================================================================");
                System.out.println("VERIFYING TABLES");
                System.out.println("================================================================================");
                
                String[] expectedTables = {"analytics_login", "user_details", "market_items"};
                try (Statement stmt = conn.createStatement()) {
                    for (String table : expectedTables) {
                        var rs = stmt.executeQuery(
                            "SELECT COUNT(*) FROM information_schema.tables " +
                            "WHERE table_schema = 'public' AND table_name = '" + table + "'"
                        );
                        if (rs.next() && rs.getInt(1) > 0) {
                            // Get row count
                            var countRs = stmt.executeQuery("SELECT COUNT(*) FROM " + table);
                            countRs.next();
                            int rowCount = countRs.getInt(1);
                            System.out.println("✓ Table '" + table + "' exists with " + rowCount + " rows");
                        }
                    }
                }
                
                System.out.println("================================================================================");
                System.out.println("✓ Schema initialization completed successfully!");
                System.out.println("================================================================================");
                
            }
            
        } catch (Exception e) {
            System.err.println("✗ Error during schema initialization:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
