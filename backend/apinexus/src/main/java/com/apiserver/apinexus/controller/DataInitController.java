package com.apiserver.apinexus.controller;

import com.apiserver.apinexus.model.*;
import com.apiserver.apinexus.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/init")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DataInitController {
    
    private final UserRepository userRepository;
    private final SubscriptionPlanRepository planRepository;
    private final CardDetailRepository cardDetailRepository;
    private final TransactionRepository transactionRepository;
    
    @PostMapping("/data")
    public ResponseEntity<Map<String, Object>> initializeData() {
        Map<String, Object> result = new HashMap<>();
        List<String> messages = new ArrayList<>();
        
        try {
            // Create demo users if they don't exist
            List<User> users = new ArrayList<>();
            if (userRepository.findByEmail("demo@nexuspay.com").isEmpty()) {
                User demoUser = new User();
                demoUser.setEmail("demo@nexuspay.com");
                demoUser.setName("Demo User");
                users.add(userRepository.save(demoUser));
                messages.add("Created demo user: demo@nexuspay.com");
            } else {
                users.add(userRepository.findByEmail("demo@nexuspay.com").get());
                messages.add("Demo user already exists");
            }
            
            if (userRepository.findByEmail("john.doe@example.com").isEmpty()) {
                User johnUser = new User();
                johnUser.setEmail("john.doe@example.com");
                johnUser.setName("John Doe");
                users.add(userRepository.save(johnUser));
                messages.add("Created user: John Doe");
            }
            
            if (userRepository.findByEmail("jane.smith@example.com").isEmpty()) {
                User janeUser = new User();
                janeUser.setEmail("jane.smith@example.com");
                janeUser.setName("Jane Smith");
                users.add(userRepository.save(janeUser));
                messages.add("Created user: Jane Smith");
            }
            
            // Create subscription plans if they don't exist
            int plansCreated = 0;
            if (planRepository.findByName("Basic Plan").isEmpty()) {
                SubscriptionPlan basicPlan = new SubscriptionPlan();
                basicPlan.setName("Basic Plan");
                basicPlan.setDescription("Perfect for individuals and small teams");
                basicPlan.setMonthlyPrice(BigDecimal.valueOf(9.99));
                basicPlan.setYearlyPrice(BigDecimal.valueOf(99.99));
                basicPlan.setFeatures("Up to 5 users, 10GB storage, Email support");
                basicPlan.setActive(true);
                planRepository.save(basicPlan);
                plansCreated++;
            }
            
            if (planRepository.findByName("Professional Plan").isEmpty()) {
                SubscriptionPlan proPlan = new SubscriptionPlan();
                proPlan.setName("Professional Plan");
                proPlan.setDescription("Ideal for growing businesses");
                proPlan.setMonthlyPrice(BigDecimal.valueOf(29.99));
                proPlan.setYearlyPrice(BigDecimal.valueOf(299.99));
                proPlan.setFeatures("Up to 50 users, 100GB storage, Priority support, API access");
                proPlan.setActive(true);
                planRepository.save(proPlan);
                plansCreated++;
            }
            
            if (planRepository.findByName("Enterprise Plan").isEmpty()) {
                SubscriptionPlan entPlan = new SubscriptionPlan();
                entPlan.setName("Enterprise Plan");
                entPlan.setDescription("For large organizations");
                entPlan.setMonthlyPrice(BigDecimal.valueOf(99.99));
                entPlan.setYearlyPrice(BigDecimal.valueOf(999.99));
                entPlan.setFeatures("Unlimited users, 1TB storage, 24/7 support, Custom integrations");
                entPlan.setActive(true);
                planRepository.save(entPlan);
                plansCreated++;
            }
            
            if (plansCreated > 0) {
                messages.add("Created " + plansCreated + " subscription plans");
            } else {
                messages.add("Subscription plans already exist");
            }
            
            result.put("success", true);
            result.put("messages", messages);
            result.put("userCount", userRepository.count());
            result.put("planCount", planRepository.count());
            result.put("cardCount", cardDetailRepository.count());
            result.put("transactionCount", transactionRepository.count());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getInitStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("userCount", userRepository.count());
        status.put("planCount", planRepository.count());
        status.put("cardCount", cardDetailRepository.count());
        status.put("transactionCount", transactionRepository.count());
        status.put("hasData", userRepository.count() > 0 && planRepository.count() > 0);
        return ResponseEntity.ok(status);
    }
}
