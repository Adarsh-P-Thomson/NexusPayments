package com.apiserver.apinexus.controller;

import com.apiserver.apinexus.repository.UserRepository;
import com.apiserver.apinexus.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HealthController {
    
    private final DataSource dataSource;
    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "NexusPay API Server");
        
        // Test PostgreSQL connection
        try {
            Connection conn = dataSource.getConnection();
            health.put("postgres", Map.of(
                "status", "UP",
                "message", "PostgreSQL connection successful",
                "userCount", userRepository.count()
            ));
            conn.close();
        } catch (Exception e) {
            health.put("postgres", Map.of(
                "status", "DOWN",
                "error", e.getMessage()
            ));
        }
        
        // Test MongoDB connection
        try {
            mongoTemplate.getDb().getName();
            health.put("mongodb", Map.of(
                "status", "UP",
                "message", "MongoDB connection successful",
                "database", mongoTemplate.getDb().getName(),
                "transactionCount", transactionRepository.count()
            ));
        } catch (Exception e) {
            health.put("mongodb", Map.of(
                "status", "DOWN",
                "error", e.getMessage()
            ));
        }
        
        return ResponseEntity.ok(health);
    }
    
    @GetMapping("/postgres")
    public ResponseEntity<Map<String, Object>> postgresHealth() {
        Map<String, Object> status = new HashMap<>();
        try {
            Connection conn = dataSource.getConnection();
            status.put("status", "UP");
            status.put("database", conn.getCatalog());
            status.put("userCount", userRepository.count());
            status.put("message", "PostgreSQL is healthy and connected");
            conn.close();
        } catch (Exception e) {
            status.put("status", "DOWN");
            status.put("error", e.getMessage());
            return ResponseEntity.status(503).body(status);
        }
        return ResponseEntity.ok(status);
    }
    
    @GetMapping("/mongodb")
    public ResponseEntity<Map<String, Object>> mongoHealth() {
        Map<String, Object> status = new HashMap<>();
        try {
            String dbName = mongoTemplate.getDb().getName();
            status.put("status", "UP");
            status.put("database", dbName);
            status.put("transactionCount", transactionRepository.count());
            status.put("message", "MongoDB is healthy and connected");
        } catch (Exception e) {
            status.put("status", "DOWN");
            status.put("error", e.getMessage());
            return ResponseEntity.status(503).body(status);
        }
        return ResponseEntity.ok(status);
    }
}
