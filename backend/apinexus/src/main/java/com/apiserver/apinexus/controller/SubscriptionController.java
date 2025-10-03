package com.apiserver.apinexus.controller;

import com.apiserver.apinexus.dto.SubscriptionRequest;
import com.apiserver.apinexus.model.UserSubscription;
import com.apiserver.apinexus.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SubscriptionController {
    
    private final SubscriptionService subscriptionService;
    
    @PostMapping
    public ResponseEntity<UserSubscription> createSubscription(@RequestBody SubscriptionRequest request) {
        UserSubscription subscription = subscriptionService.createSubscription(
            request.getUserId(),
            request.getPlanId(),
            request.getBillingCycle()
        );
        return ResponseEntity.ok(subscription);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserSubscription>> getUserSubscriptions(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getUserSubscriptions(userId));
    }
    
    @GetMapping("/user/{userId}/has-active")
    public ResponseEntity<Map<String, Boolean>> checkActiveSubscription(@PathVariable Long userId) {
        boolean hasActive = subscriptionService.hasActiveSubscription(userId);
        return ResponseEntity.ok(Map.of("hasActiveSubscription", hasActive));
    }
    
    @DeleteMapping("/{subscriptionId}")
    public ResponseEntity<Void> cancelSubscription(@PathVariable Long subscriptionId) {
        subscriptionService.cancelSubscription(subscriptionId);
        return ResponseEntity.ok().build();
    }
}
