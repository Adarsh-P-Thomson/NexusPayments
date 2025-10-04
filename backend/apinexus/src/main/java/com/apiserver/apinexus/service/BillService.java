package com.apiserver.apinexus.service;

import com.apiserver.apinexus.model.Bill;
import com.apiserver.apinexus.model.UserSubscription;
import com.apiserver.apinexus.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillService {
    
    private final BillRepository billRepository;
    
    @Transactional
    public Bill generateBill(UserSubscription subscription) {
        Bill bill = new Bill();
        bill.setUser(subscription.getUser());
        bill.setSubscription(subscription);
        bill.setBillNumber("BILL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        bill.setAmount(subscription.getAmount());
        bill.setStatus(Bill.BillStatus.PENDING);
        
        // Set billing period
        LocalDateTime now = LocalDateTime.now();
        bill.setBillingPeriodStart(now);
        
        if (subscription.getBillingCycle() == UserSubscription.BillingCycle.MONTHLY) {
            bill.setBillingPeriodEnd(now.plusMonths(1));
            bill.setDueDate(now.plusDays(7)); // 7 days to pay
        } else {
            bill.setBillingPeriodEnd(now.plusYears(1));
            bill.setDueDate(now.plusDays(14)); // 14 days to pay for yearly
        }
        
        return billRepository.save(bill);
    }
    
    public List<Bill> getUserBills(Long userId) {
        return billRepository.findByUserId(userId);
    }
    
    public List<Bill> getPendingBills() {
        return billRepository.findByStatus(Bill.BillStatus.PENDING);
    }
}
