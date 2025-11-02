package com.apiserver.apinexus.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "monthly_price", precision = 10, scale = 2)
    private BigDecimal monthlyPrice;
    
    @Column(name = "yearly_price", precision = 10, scale = 2)
    private BigDecimal yearlyPrice;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String features; // JSON array of features
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @Column(name = "is_default")
    private Boolean isDefault = false; // Mark if this is a default plan (Monthly/Yearly)
    
    @Column(name = "plan_type")
    private String planType; // BASIC, PREMIUM, ENTERPRISE, CUSTOM
    
    @Column(name = "discount_percentage")
    private Integer discountPercentage = 0; // For offer plans
    
    @Column(name = "offer_valid_until")
    private LocalDateTime offerValidUntil; // Offer expiry date
    
    @Column(name = "max_users")
    private Integer maxUsers; // Maximum users allowed
    
    @Column(name = "max_bills_per_month")
    private Integer maxBillsPerMonth; // Bill limit per month
    
    @Column(name = "priority_support")
    private Boolean prioritySupport = false;
    
    @Column(name = "custom_branding")
    private Boolean customBranding = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
