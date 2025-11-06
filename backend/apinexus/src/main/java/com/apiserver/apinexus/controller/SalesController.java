package com.apiserver.apinexus.controller;

import com.apiserver.apinexus.dto.*;
import com.apiserver.apinexus.model.Sale;
import com.apiserver.apinexus.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "*")
public class SalesController {
    
    @Autowired
    private SalesService salesService;
    
    /**
     * Get all sales
     * GET /api/sales
     */
    @GetMapping
    public ResponseEntity<List<Sale>> getAllSales(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<Sale> sales = startDate != null && endDate != null 
            ? salesService.getSalesByDateRange(startDate, endDate)
            : salesService.getAllSales();
        
        return ResponseEntity.ok(sales);
    }
    
    /**
     * Get sales analytics summary
     * GET /api/sales/analytics
     */
    @GetMapping("/analytics")
    public ResponseEntity<SalesAnalyticsDTO> getSalesAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        SalesAnalyticsDTO analytics = salesService.getSalesAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }
    
    /**
     * Get sales grouped by product
     * GET /api/sales/by-product
     */
    @GetMapping("/by-product")
    public ResponseEntity<List<ProductSalesDTO>> getSalesByProduct(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<ProductSalesDTO> productSales = salesService.getSalesByProduct(startDate, endDate);
        return ResponseEntity.ok(productSales);
    }
    
    /**
     * Get sales grouped by category
     * GET /api/sales/by-category
     */
    @GetMapping("/by-category")
    public ResponseEntity<List<CategorySalesDTO>> getSalesByCategory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<CategorySalesDTO> categorySales = salesService.getSalesByCategory(startDate, endDate);
        return ResponseEntity.ok(categorySales);
    }
    
    /**
     * Get sales by time period (daily, weekly, monthly, yearly)
     * GET /api/sales/by-period?period=daily&startDate=...&endDate=...
     */
    @GetMapping("/by-period")
    public ResponseEntity<List<TimePeriodSalesDTO>> getSalesByTimePeriod(
            @RequestParam(defaultValue = "daily") String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<TimePeriodSalesDTO> periodSales = salesService.getSalesByTimePeriod(period, startDate, endDate);
        return ResponseEntity.ok(periodSales);
    }
    
    /**
     * Get top selling products
     * GET /api/sales/top-products?limit=10
     */
    @GetMapping("/top-products")
    public ResponseEntity<List<ProductSalesDTO>> getTopSellingProducts(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<ProductSalesDTO> topProducts = salesService.getTopSellingProducts(limit, startDate, endDate);
        return ResponseEntity.ok(topProducts);
    }
    
    /**
     * Get sales by region
     * GET /api/sales/region/{region}
     */
    @GetMapping("/region/{region}")
    public ResponseEntity<List<Sale>> getSalesByRegion(@PathVariable String region) {
        List<Sale> sales = salesService.getSalesByRegion(region);
        return ResponseEntity.ok(sales);
    }
    
    /**
     * Create a new sale
     * POST /api/sales
     */
    @PostMapping
    public ResponseEntity<Sale> createSale(@RequestBody Sale sale) {
        try {
            Sale createdSale = salesService.createSale(sale);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSale);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
