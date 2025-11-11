package com.apiserver.apinexus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPerformanceDTO {
    private Long productId;
    private String productName;
    private String category;
    private Integer salesCount;
    private Integer totalQuantitySold;
    private Double totalRevenue;
    private Double avgOrderValue;
    private Double avgUnitPrice;
    private Integer daysSinceFirstSale;
    private Integer daysSinceLastSale;
    private String performanceStatus; // TOP_PERFORMER, STEADY, SLOW_MOVING, STAGNANT
    private Double velocityScore; // Sales per day
}
