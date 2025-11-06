package com.apiserver.apinexus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimePeriodSalesDTO {
    private String period; // e.g., "2025-11-01", "2025-11", "Week 44"
    private Double revenue;
    private Integer salesCount;
    private Integer quantity;
}
