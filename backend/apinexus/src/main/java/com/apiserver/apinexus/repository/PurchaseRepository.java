package com.apiserver.apinexus.repository;

import com.apiserver.apinexus.model.Purchase;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PurchaseRepository extends MongoRepository<Purchase, String> {
    
    // Find all purchases by user ID
    List<Purchase> findByUserId(Long userId);
    
    // Find all purchases by username
    List<Purchase> findByUsername(String username);
    
    // Find purchases by premium user status
    List<Purchase> findByIsPremiumUser(Boolean isPremiumUser);
    
    // Find purchases by status
    List<Purchase> findByStatus(String status);
    
    // Find purchases within date range
    List<Purchase> findByPurchaseDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find purchases by user and status
    List<Purchase> findByUserIdAndStatus(Long userId, String status);
}
