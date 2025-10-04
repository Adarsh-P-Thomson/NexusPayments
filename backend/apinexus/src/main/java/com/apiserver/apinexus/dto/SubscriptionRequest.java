package com.apiserver.apinexus.dto;

import com.apiserver.apinexus.model.UserSubscription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequest {
    private Long userId;
    private Long planId;
    private UserSubscription.BillingCycle billingCycle;
}
