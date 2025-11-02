package com.apiserver.apinexus.controller;

import com.apiserver.apinexus.dto.SubscriptionPlanDTO;
import com.apiserver.apinexus.model.SubscriptionPlan;
import com.apiserver.apinexus.service.SubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/subscription-plans")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SubscriptionPlanController {
    
    private final SubscriptionPlanService subscriptionPlanService;
    
    @GetMapping
    public ResponseEntity<List<SubscriptionPlanDTO>> getAllActivePlans() {
        List<SubscriptionPlanDTO> plans = subscriptionPlanService.getAllActivePlans().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(plans);
    }
    
    @GetMapping("/default")
    public ResponseEntity<List<SubscriptionPlanDTO>> getDefaultPlans() {
        List<SubscriptionPlanDTO> plans = subscriptionPlanService.getDefaultPlans().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(plans);
    }
    
    @GetMapping("/offers")
    public ResponseEntity<List<SubscriptionPlanDTO>> getOfferPlans() {
        List<SubscriptionPlanDTO> plans = subscriptionPlanService.getOfferPlans().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(plans);
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<SubscriptionPlanDTO>> getPlansByType(@PathVariable String type) {
        List<SubscriptionPlanDTO> plans = subscriptionPlanService.getPlansByType(type.toUpperCase()).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(plans);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionPlanDTO> getPlanById(@PathVariable Long id) {
        return subscriptionPlanService.getPlanById(id)
            .map(plan -> ResponseEntity.ok(convertToDTO(plan)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<SubscriptionPlanDTO> createPlan(@RequestBody SubscriptionPlanDTO planDTO) {
        try {
            SubscriptionPlan plan = convertFromDTO(planDTO);
            SubscriptionPlan created = subscriptionPlanService.createPlan(plan);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(created));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionPlanDTO> updatePlan(@PathVariable Long id, @RequestBody SubscriptionPlanDTO planDTO) {
        try {
            SubscriptionPlan plan = convertFromDTO(planDTO);
            SubscriptionPlan updated = subscriptionPlanService.updatePlan(id, plan);
            return ResponseEntity.ok(convertToDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        try {
            subscriptionPlanService.deletePlan(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activatePlan(@PathVariable Long id) {
        try {
            subscriptionPlanService.activatePlan(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    private SubscriptionPlanDTO convertToDTO(SubscriptionPlan plan) {
        return new SubscriptionPlanDTO(
            plan.getId(),
            plan.getName(),
            plan.getDescription(),
            plan.getMonthlyPrice(),
            plan.getYearlyPrice(),
            plan.getFeatures(),
            plan.getActive(),
            plan.getIsDefault(),
            plan.getPlanType(),
            plan.getDiscountPercentage(),
            plan.getOfferValidUntil(),
            plan.getMaxUsers(),
            plan.getMaxBillsPerMonth(),
            plan.getPrioritySupport(),
            plan.getCustomBranding(),
            plan.getCreatedAt(),
            plan.getUpdatedAt()
        );
    }
    
    private SubscriptionPlan convertFromDTO(SubscriptionPlanDTO dto) {
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setId(dto.getId());
        plan.setName(dto.getName());
        plan.setDescription(dto.getDescription());
        plan.setMonthlyPrice(dto.getMonthlyPrice());
        plan.setYearlyPrice(dto.getYearlyPrice());
        plan.setFeatures(dto.getFeatures());
        plan.setActive(dto.getActive());
        plan.setIsDefault(dto.getIsDefault());
        plan.setPlanType(dto.getPlanType());
        plan.setDiscountPercentage(dto.getDiscountPercentage());
        plan.setOfferValidUntil(dto.getOfferValidUntil());
        plan.setMaxUsers(dto.getMaxUsers());
        plan.setMaxBillsPerMonth(dto.getMaxBillsPerMonth());
        plan.setPrioritySupport(dto.getPrioritySupport());
        plan.setCustomBranding(dto.getCustomBranding());
        return plan;
    }
}
