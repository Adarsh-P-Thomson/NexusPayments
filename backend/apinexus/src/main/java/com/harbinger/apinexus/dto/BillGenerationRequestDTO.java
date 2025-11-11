package com.apiserver.apinexus.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillGenerationRequestDTO {
    
    public enum TimePeriod {
        WEEK,
        MONTH,
        YEAR,
        CUSTOM
    }
    
    private TimePeriod period;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long customerId;  // Optional - filter by specific customer
    private String customerName;  // Optional - filter by customer name
}
