package com.apiserver.apinexus.service;

import com.apiserver.apinexus.model.SubscriptionPlan;
import com.apiserver.apinexus.model.User;
import com.apiserver.apinexus.model.UserSubscription;
import com.apiserver.apinexus.repository.SubscriptionPlanRepository;
import com.apiserver.apinexus.repository.UserRepository;
import com.apiserver.apinexus.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final UserRepository userRepository;
    private final SubscriptionPlanRepository planRepository;
    
    @Transactional
    public UserSubscription createSubscription(Long userId, Long planId, UserSubscription.BillingCycle billingCycle) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        SubscriptionPlan plan = planRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Subscription plan not found"));
        
        UserSubscription subscription = new UserSubscription();
        subscription.setUser(user);
        subscription.setSubscriptionPlan(plan);
        subscription.setBillingCycle(billingCycle);
        subscription.setStatus(UserSubscription.SubscriptionStatus.ACTIVE);
        subscription.setStartDate(LocalDateTime.now());
        
        // Set amount based on billing cycle
        BigDecimal amount = billingCycle == UserSubscription.BillingCycle.MONTHLY 
            ? plan.getMonthlyPrice() 
            : plan.getYearlyPrice();
        subscription.setAmount(amount);
        
        // Calculate next billing date
        LocalDateTime nextBillingDate = billingCycle == UserSubscription.BillingCycle.MONTHLY
            ? LocalDateTime.now().plusMonths(1)
            : LocalDateTime.now().plusYears(1);
        subscription.setNextBillingDate(nextBillingDate);
        
        return userSubscriptionRepository.save(subscription);
    }
    
    public List<UserSubscription> getUserSubscriptions(Long userId) {
        return userSubscriptionRepository.findByUserId(userId);
    }
    
    public boolean hasActiveSubscription(Long userId) {
        return userSubscriptionRepository
            .findByUserIdAndStatus(userId, UserSubscription.SubscriptionStatus.ACTIVE)
            .isPresent();
    }
    
    @Transactional
    public void cancelSubscription(Long subscriptionId) {
        UserSubscription subscription = userSubscriptionRepository.findById(subscriptionId)
            .orElseThrow(() -> new RuntimeException("Subscription not found"));
        subscription.setStatus(UserSubscription.SubscriptionStatus.CANCELLED);
        subscription.setEndDate(LocalDateTime.now());
        userSubscriptionRepository.save(subscription);
    }
}
