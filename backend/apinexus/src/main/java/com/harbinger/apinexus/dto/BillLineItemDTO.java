package com.apiserver.apinexus.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillLineItemDTO {
    private String productName;
    private int quantity;
    private double unitPrice;
    private double subtotal;  // quantity * unitPrice
    private double discount;
    private double total;  // subtotal - discount
}
