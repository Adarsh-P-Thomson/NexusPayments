package com.apiserver.apinexus.service;

import com.apiserver.apinexus.dto.*;
import com.apiserver.apinexus.model.Sale;
import com.apiserver.apinexus.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SalesService {
    
    @Autowired
    private SaleRepository saleRepository;
    
    /**
     * Get all sales
     */
    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }
    
    /**
     * Get sales by date range
     */
    public List<Sale> getSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return saleRepository.findBySaleDateBetween(startDate, endDate);
    }
    
    /**
     * Get sales analytics summary
     */
    public SalesAnalyticsDTO getSalesAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        List<Sale> sales = startDate != null && endDate != null 
            ? getSalesByDateRange(startDate, endDate)
            : getAllSales();
        
        double totalRevenue = sales.stream()
            .mapToDouble(Sale::getFinalAmount)
            .sum();
        
        int totalSales = sales.size();
        
        int totalQuantity = sales.stream()
            .mapToInt(Sale::getQuantity)
            .sum();
        
        double averageOrderValue = totalSales > 0 ? totalRevenue / totalSales : 0.0;
        
        double totalDiscounts = sales.stream()
            .mapToDouble(sale -> sale.getDiscountApplied() != null ? sale.getDiscountApplied() : 0.0)
            .sum();
        
        int premiumSales = (int) sales.stream()
            .filter(sale -> sale.getIsPremiumCustomer() != null && sale.getIsPremiumCustomer())
            .count();
        
        int regularSales = totalSales - premiumSales;
        
        return new SalesAnalyticsDTO(
            Math.round(totalRevenue * 100.0) / 100.0,
            totalSales,
            totalQuantity,
            Math.round(averageOrderValue * 100.0) / 100.0,
            Math.round(totalDiscounts * 100.0) / 100.0,
            premiumSales,
            regularSales
        );
    }
    
    /**
     * Get sales by product
     */
    public List<ProductSalesDTO> getSalesByProduct(LocalDateTime startDate, LocalDateTime endDate) {
        List<Sale> sales = startDate != null && endDate != null 
            ? getSalesByDateRange(startDate, endDate)
            : getAllSales();
        
        Map<Long, ProductSalesDTO> productSalesMap = new HashMap<>();
        
        for (Sale sale : sales) {
            Long productId = sale.getProductId();
            ProductSalesDTO dto = productSalesMap.getOrDefault(productId, 
                new ProductSalesDTO(productId, sale.getProductName(), sale.getCategory(), 0, 0.0, 0));
            
            dto.setTotalQuantity(dto.getTotalQuantity() + sale.getQuantity());
            dto.setTotalRevenue(dto.getTotalRevenue() + sale.getFinalAmount());
            dto.setSalesCount(dto.getSalesCount() + 1);
            
            productSalesMap.put(productId, dto);
        }
        
        return productSalesMap.values().stream()
            .sorted((a, b) -> Double.compare(b.getTotalRevenue(), a.getTotalRevenue()))
            .collect(Collectors.toList());
    }
    
    /**
     * Get sales by category
     */
    public List<CategorySalesDTO> getSalesByCategory(LocalDateTime startDate, LocalDateTime endDate) {
        List<Sale> sales = startDate != null && endDate != null 
            ? getSalesByDateRange(startDate, endDate)
            : getAllSales();
        
        Map<String, CategorySalesDTO> categorySalesMap = new HashMap<>();
        
        for (Sale sale : sales) {
            String category = sale.getCategory();
            CategorySalesDTO dto = categorySalesMap.getOrDefault(category,
                new CategorySalesDTO(category, 0, 0.0, 0));
            
            dto.setTotalQuantity(dto.getTotalQuantity() + sale.getQuantity());
            dto.setTotalRevenue(dto.getTotalRevenue() + sale.getFinalAmount());
            dto.setSalesCount(dto.getSalesCount() + 1);
            
            categorySalesMap.put(category, dto);
        }
        
        return categorySalesMap.values().stream()
            .sorted((a, b) -> Double.compare(b.getTotalRevenue(), a.getTotalRevenue()))
            .collect(Collectors.toList());
    }
    
    /**
     * Get sales by time period (daily, weekly, monthly)
     */
    public List<TimePeriodSalesDTO> getSalesByTimePeriod(String period, LocalDateTime startDate, LocalDateTime endDate) {
        List<Sale> sales = startDate != null && endDate != null 
            ? getSalesByDateRange(startDate, endDate)
            : getAllSales();
        
        Map<String, TimePeriodSalesDTO> periodSalesMap = new HashMap<>();
        DateTimeFormatter formatter;
        
        switch (period.toLowerCase()) {
            case "daily":
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                break;
            case "weekly":
                formatter = DateTimeFormatter.ofPattern("yyyy-'W'ww");
                break;
            case "monthly":
                formatter = DateTimeFormatter.ofPattern("yyyy-MM");
                break;
            case "yearly":
                formatter = DateTimeFormatter.ofPattern("yyyy");
                break;
            default:
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        }
        
        for (Sale sale : sales) {
            String periodKey = sale.getSaleDate().format(formatter);
            TimePeriodSalesDTO dto = periodSalesMap.getOrDefault(periodKey,
                new TimePeriodSalesDTO(periodKey, 0.0, 0, 0));
            
            dto.setRevenue(dto.getRevenue() + sale.getFinalAmount());
            dto.setSalesCount(dto.getSalesCount() + 1);
            dto.setQuantity(dto.getQuantity() + sale.getQuantity());
            
            periodSalesMap.put(periodKey, dto);
        }
        
        return periodSalesMap.values().stream()
            .sorted(Comparator.comparing(TimePeriodSalesDTO::getPeriod))
            .collect(Collectors.toList());
    }
    
    /**
     * Create a new sale
     */
    public Sale createSale(Sale sale) {
        if (sale.getSaleDate() == null) {
            sale.setSaleDate(LocalDateTime.now());
        }
        
        // Calculate totals
        if (sale.getQuantity() != null && sale.getUnitPrice() != null) {
            sale.setTotalPrice(sale.getQuantity() * sale.getUnitPrice());
            
            // Apply discount for premium customers
            if (sale.getIsPremiumCustomer() != null && sale.getIsPremiumCustomer()) {
                sale.setDiscountApplied(sale.getTotalPrice() * 0.20);
            } else {
                sale.setDiscountApplied(0.0);
            }
            
            sale.setFinalAmount(sale.getTotalPrice() - sale.getDiscountApplied());
        }
        
        return saleRepository.save(sale);
    }
    
    /**
     * Get sales by region
     */
    public List<Sale> getSalesByRegion(String region) {
        return saleRepository.findByRegion(region);
    }
    
    /**
     * Get top selling products
     */
    public List<ProductSalesDTO> getTopSellingProducts(int limit, LocalDateTime startDate, LocalDateTime endDate) {
        List<ProductSalesDTO> productSales = getSalesByProduct(startDate, endDate);
        return productSales.stream()
            .limit(limit)
            .collect(Collectors.toList());
    }
}
