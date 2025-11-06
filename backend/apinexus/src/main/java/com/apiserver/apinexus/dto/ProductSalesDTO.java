package com.apiserver.apinexus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSalesDTO {
    private Long productId;
    private String productName;
    private String category;
    private Integer totalQuantity;
    private Double totalRevenue;
    private Integer salesCount;
}
