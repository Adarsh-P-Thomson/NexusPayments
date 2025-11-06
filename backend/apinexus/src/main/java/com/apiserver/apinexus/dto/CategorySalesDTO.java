package com.apiserver.apinexus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorySalesDTO {
    private String category;
    private Integer totalQuantity;
    private Double totalRevenue;
    private Integer salesCount;
}
