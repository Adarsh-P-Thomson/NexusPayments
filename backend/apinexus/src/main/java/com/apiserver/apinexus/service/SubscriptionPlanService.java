package com.apiserver.apinexus.service;

import com.apiserver.apinexus.model.SubscriptionPlan;
import com.apiserver.apinexus.repository.SubscriptionPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubscriptionPlanService {
    
    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;
    
    /**
     * Get all active subscription plans
     */
    public List<SubscriptionPlan> getAllActivePlans() {
        return subscriptionPlanRepository.findByActiveTrue();
    }
    
    /**
     * Get all default plans (Monthly/Yearly standard plans)
     */
    public List<SubscriptionPlan> getDefaultPlans() {
        return subscriptionPlanRepository.findByActiveTrueAndIsDefaultTrue();
    }
    
    /**
     * Get all offer/special plans
     */
    public List<SubscriptionPlan> getOfferPlans() {
        List<SubscriptionPlan> offers = subscriptionPlanRepository.findByActiveTrueAndIsDefaultFalse();
        // Filter out expired offers
        LocalDateTime now = LocalDateTime.now();
        return offers.stream()
            .filter(plan -> plan.getOfferValidUntil() == null || plan.getOfferValidUntil().isAfter(now))
            .collect(Collectors.toList());
    }
    
    /**
     * Get plans by type (BASIC, PREMIUM, ENTERPRISE)
     */
    public List<SubscriptionPlan> getPlansByType(String planType) {
        return subscriptionPlanRepository.findByActiveTrueAndPlanType(planType);
    }
    
    /**
     * Get plan by ID
     */
    public Optional<SubscriptionPlan> getPlanById(Long id) {
        return subscriptionPlanRepository.findById(id);
    }
    
    /**
     * Create a new subscription plan
     */
    @Transactional
    public SubscriptionPlan createPlan(SubscriptionPlan plan) {
        plan.setCreatedAt(LocalDateTime.now());
        plan.setUpdatedAt(LocalDateTime.now());
        return subscriptionPlanRepository.save(plan);
    }
    
    /**
     * Update existing subscription plan
     */
    @Transactional
    public SubscriptionPlan updatePlan(Long id, SubscriptionPlan updatedPlan) {
        Optional<SubscriptionPlan> existing = subscriptionPlanRepository.findById(id);
        if (existing.isPresent()) {
            SubscriptionPlan plan = existing.get();
            plan.setName(updatedPlan.getName());
            plan.setDescription(updatedPlan.getDescription());
            plan.setMonthlyPrice(updatedPlan.getMonthlyPrice());
            plan.setYearlyPrice(updatedPlan.getYearlyPrice());
            plan.setFeatures(updatedPlan.getFeatures());
            plan.setActive(updatedPlan.getActive());
            plan.setIsDefault(updatedPlan.getIsDefault());
            plan.setPlanType(updatedPlan.getPlanType());
            plan.setDiscountPercentage(updatedPlan.getDiscountPercentage());
            plan.setOfferValidUntil(updatedPlan.getOfferValidUntil());
            plan.setMaxUsers(updatedPlan.getMaxUsers());
            plan.setMaxBillsPerMonth(updatedPlan.getMaxBillsPerMonth());
            plan.setPrioritySupport(updatedPlan.getPrioritySupport());
            plan.setCustomBranding(updatedPlan.getCustomBranding());
            plan.setUpdatedAt(LocalDateTime.now());
            return subscriptionPlanRepository.save(plan);
        }
        throw new RuntimeException("Subscription plan not found with id: " + id);
    }
    
    /**
     * Delete subscription plan (soft delete by setting active = false)
     */
    @Transactional
    public void deletePlan(Long id) {
        Optional<SubscriptionPlan> existing = subscriptionPlanRepository.findById(id);
        if (existing.isPresent()) {
            SubscriptionPlan plan = existing.get();
            plan.setActive(false);
            plan.setUpdatedAt(LocalDateTime.now());
            subscriptionPlanRepository.save(plan);
        } else {
            throw new RuntimeException("Subscription plan not found with id: " + id);
        }
    }
    
    /**
     * Activate subscription plan
     */
    @Transactional
    public void activatePlan(Long id) {
        Optional<SubscriptionPlan> existing = subscriptionPlanRepository.findById(id);
        if (existing.isPresent()) {
            SubscriptionPlan plan = existing.get();
            plan.setActive(true);
            plan.setUpdatedAt(LocalDateTime.now());
            subscriptionPlanRepository.save(plan);
        } else {
            throw new RuntimeException("Subscription plan not found with id: " + id);
        }
    }
}
