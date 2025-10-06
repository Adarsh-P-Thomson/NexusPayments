package com.apiserver.apinexus.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "card_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDetail {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "card_number_last4", nullable = false, length = 4)
    private String cardNumberLast4;
    
    @Column(name = "card_holder_name", nullable = false)
    private String cardHolderName;
    
    @Column(name = "card_type", length = 50)
    private String cardType;
    
    @Column(name = "expiry_month", nullable = false)
    private Integer expiryMonth;
    
    @Column(name = "expiry_year", nullable = false)
    private Integer expiryYear;
    
    @Column(name = "is_default")
    private Boolean isDefault = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
