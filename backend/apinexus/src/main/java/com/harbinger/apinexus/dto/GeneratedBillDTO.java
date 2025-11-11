package com.apiserver.apinexus.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedBillDTO {
    private String billNumber;
    private LocalDateTime generatedDate;
    private String period;  // "Week of 2025-01-01", "January 2025", etc.
    private LocalDate periodStartDate;
    private LocalDate periodEndDate;
    
    // Customer info
    private String customerName;
    private Long customerId;
    
    // Line items
    private List<BillLineItemDTO> lineItems;
    
    // Totals
    private double subtotal;
    private double totalDiscount;
    private double taxableAmount;  // subtotal - discount
    private double taxRate;  // e.g., 0.10 for 10%
    private double taxAmount;
    private double grandTotal;
    
    // Statistics
    private int totalTransactions;
    private int totalItemsSold;
    private String paymentMethod;  // Most common payment method
}
