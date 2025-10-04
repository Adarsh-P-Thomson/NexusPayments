package com.apiserver.apinexus.controller;

import com.apiserver.apinexus.dto.SubscriptionPlanDTO;
import com.apiserver.apinexus.model.SubscriptionPlan;
import com.apiserver.apinexus.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/subscription-plans")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SubscriptionPlanController {
    
    private final SubscriptionPlanRepository planRepository;
    
    @GetMapping
    public ResponseEntity<List<SubscriptionPlanDTO>> getAllPlans() {
        List<SubscriptionPlanDTO> plans = planRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(plans);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<SubscriptionPlanDTO>> getActivePlans() {
        List<SubscriptionPlanDTO> plans = planRepository.findByActiveTrue().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(plans);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionPlanDTO> getPlanById(@PathVariable Long id) {
        return planRepository.findById(id)
            .map(plan -> ResponseEntity.ok(convertToDTO(plan)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<SubscriptionPlanDTO> createPlan(@RequestBody SubscriptionPlanDTO planDTO) {
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName(planDTO.getName());
        plan.setDescription(planDTO.getDescription());
        plan.setMonthlyPrice(planDTO.getMonthlyPrice());
        plan.setYearlyPrice(planDTO.getYearlyPrice());
        plan.setFeatures(planDTO.getFeatures());
        plan.setActive(planDTO.getActive());
        
        SubscriptionPlan savedPlan = planRepository.save(plan);
        return ResponseEntity.ok(convertToDTO(savedPlan));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionPlanDTO> updatePlan(@PathVariable Long id, @RequestBody SubscriptionPlanDTO planDTO) {
        return planRepository.findById(id)
            .map(plan -> {
                plan.setName(planDTO.getName());
                plan.setDescription(planDTO.getDescription());
                plan.setMonthlyPrice(planDTO.getMonthlyPrice());
                plan.setYearlyPrice(planDTO.getYearlyPrice());
                plan.setFeatures(planDTO.getFeatures());
                plan.setActive(planDTO.getActive());
                return ResponseEntity.ok(convertToDTO(planRepository.save(plan)));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        if (planRepository.existsById(id)) {
            planRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    private SubscriptionPlanDTO convertToDTO(SubscriptionPlan plan) {
        return new SubscriptionPlanDTO(
            plan.getId(),
            plan.getName(),
            plan.getDescription(),
            plan.getMonthlyPrice(),
            plan.getYearlyPrice(),
            plan.getFeatures(),
            plan.getActive()
        );
    }
}
