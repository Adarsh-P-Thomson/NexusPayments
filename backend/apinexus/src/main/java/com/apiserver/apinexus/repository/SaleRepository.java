package com.apiserver.apinexus.repository;

import com.apiserver.apinexus.model.Sale;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends MongoRepository<Sale, String> {
    
    // Find sales by product
    List<Sale> findByProductId(Long productId);
    List<Sale> findByProductName(String productName);
    
    // Find sales by category
    List<Sale> findByCategory(String category);
    
    // Find sales by customer
    List<Sale> findByCustomerId(Long customerId);
    List<Sale> findByIsPremiumCustomer(Boolean isPremiumCustomer);
    
    // Find sales by date range
    List<Sale> findBySaleDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find sales by region
    List<Sale> findByRegion(String region);
    
    // Find sales by payment method
    List<Sale> findByPaymentMethod(String paymentMethod);
    
    // Custom aggregation queries
    @Query(value = "{}", fields = "{ 'product_name': 1, 'quantity': 1, 'final_amount': 1, 'sale_date': 1 }")
    List<Sale> findAllSalesBasicInfo();
    
    // Find sales with minimum amount
    List<Sale> findByFinalAmountGreaterThanEqual(Double minAmount);
    
    // Find recent sales
    List<Sale> findTop10BySaleDateOrderBySaleDateDesc(LocalDateTime saleDate);
}
