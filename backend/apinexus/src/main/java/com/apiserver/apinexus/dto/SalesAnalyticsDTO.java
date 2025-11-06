package com.apiserver.apinexus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesAnalyticsDTO {
    private Double totalRevenue;
    private Integer totalSales;
    private Integer totalQuantitySold;
    private Double averageOrderValue;
    private Double totalDiscounts;
    private Integer premiumCustomerSales;
    private Integer regularCustomerSales;
}
