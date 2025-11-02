package com.apiserver.apinexus.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
public class DatabaseConfig {

    @Bean
    CommandLineRunner testDatabaseConnections(DataSource dataSource, MongoTemplate mongoTemplate) {
        return args -> {
            System.out.println("=".repeat(80));
            System.out.println("DATABASE CONNECTION TESTS");
            System.out.println("=".repeat(80));
            
            // Test PostgreSQL Connection
            try (Connection connection = dataSource.getConnection()) {
                System.out.println("✓ PostgreSQL Connection: SUCCESS");
                System.out.println("  Database: " + connection.getCatalog());
                System.out.println("  URL: " + connection.getMetaData().getURL());
                System.out.println("  User: " + connection.getMetaData().getUserName());
            } catch (Exception e) {
                System.err.println("✗ PostgreSQL Connection: FAILED");
                System.err.println("  Error: " + e.getMessage());
                e.printStackTrace();
            }
            
            System.out.println("-".repeat(80));
            
            // Test MongoDB Connection
            try {
                String dbName = mongoTemplate.getDb().getName();
                System.out.println("✓ MongoDB Connection: SUCCESS");
                System.out.println("  Database: " + dbName);
                
                // Try to get collection names to verify connection
                var collections = mongoTemplate.getDb().listCollectionNames();
                System.out.println("  Collections: " + String.join(", ", collections.into(new java.util.ArrayList<>())));
            } catch (Exception e) {
                System.err.println("✗ MongoDB Connection: FAILED");
                System.err.println("  Error: " + e.getMessage());
                e.printStackTrace();
            }
            
            System.out.println("=".repeat(80));
        };
    }
}
