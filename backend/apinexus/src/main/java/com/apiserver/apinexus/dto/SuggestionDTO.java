package com.apiserver.apinexus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionDTO {
    private String category; // INVENTORY, PRICING, MARKETING, PRODUCT, REGIONAL
    private String priority; // HIGH, MEDIUM, LOW
    private String title;
    private String description;
    private String actionable;
    private Double impactScore; // 0-100 estimated business impact
    private String metric; // e.g., "Revenue", "Sales Count", "Margin"
    private Double currentValue;
    private Double potentialValue;
}
