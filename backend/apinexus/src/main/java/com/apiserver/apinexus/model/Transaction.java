package com.apiserver.apinexus.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    
    @Id
    private String id;
    
    private Long userId;
    
    private Long billId;
    
    private String transactionId;
    
    private BigDecimal amount;
    
    private TransactionStatus status;
    
    private String paymentMethod;
    
    private LocalDateTime transactionDate;
    
    private LocalDateTime scheduledRetryDate;
    
    private String failureReason;
    
    private Integer retryCount = 0;
    
    private Map<String, Object> metadata;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    public enum TransactionStatus {
        PENDING, SUCCESS, FAILED, RETRYING, CANCELLED
    }
}
