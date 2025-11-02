package com.apiserver.apinexus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal monthlyPrice;
    private BigDecimal yearlyPrice;
    private String features;
    private Boolean active;
    private Boolean isDefault;
    private String planType;
    private Integer discountPercentage;
    private LocalDateTime offerValidUntil;
    private Integer maxUsers;
    private Integer maxBillsPerMonth;
    private Boolean prioritySupport;
    private Boolean customBranding;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
