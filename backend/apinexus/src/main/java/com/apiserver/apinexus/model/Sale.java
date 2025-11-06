package com.apiserver.apinexus.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * MongoDB Document for storing sales logs
 * Collection: sales
 */
@Document(collection = "sales")
public class Sale {
    
    @Id
    private String id;
    
    @Field("product_id")
    private Long productId;
    
    @Field("product_name")
    private String productName;
    
    @Field("category")
    private String category;
    
    @Field("quantity")
    private Integer quantity;
    
    @Field("unit_price")
    private Double unitPrice;
    
    @Field("total_price")
    private Double totalPrice;
    
    @Field("customer_id")
    private Long customerId;
    
    @Field("customer_name")
    private String customerName;
    
    @Field("is_premium_customer")
    private Boolean isPremiumCustomer;
    
    @Field("discount_applied")
    private Double discountApplied;
    
    @Field("final_amount")
    private Double finalAmount;
    
    @Field("payment_method")
    private String paymentMethod;
    
    @Field("sale_date")
    private LocalDateTime saleDate;
    
    @Field("region")
    private String region;
    
    @Field("salesperson")
    private String salesperson;
    
    @Field("notes")
    private String notes;
    
    // Constructors
    public Sale() {}
    
    public Sale(Long productId, String productName, String category, Integer quantity, 
                Double unitPrice, Long customerId, String customerName, Boolean isPremiumCustomer,
                String paymentMethod, String region) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = quantity * unitPrice;
        this.customerId = customerId;
        this.customerName = customerName;
        this.isPremiumCustomer = isPremiumCustomer;
        this.discountApplied = isPremiumCustomer ? (totalPrice * 0.20) : 0.0;
        this.finalAmount = totalPrice - discountApplied;
        this.paymentMethod = paymentMethod;
        this.saleDate = LocalDateTime.now();
        this.region = region;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { 
        this.quantity = quantity;
        if (unitPrice != null) {
            this.totalPrice = quantity * unitPrice;
            this.finalAmount = totalPrice - (discountApplied != null ? discountApplied : 0.0);
        }
    }
    
    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { 
        this.unitPrice = unitPrice;
        if (quantity != null) {
            this.totalPrice = quantity * unitPrice;
            this.finalAmount = totalPrice - (discountApplied != null ? discountApplied : 0.0);
        }
    }
    
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public Boolean getIsPremiumCustomer() { return isPremiumCustomer; }
    public void setIsPremiumCustomer(Boolean isPremiumCustomer) { this.isPremiumCustomer = isPremiumCustomer; }
    
    public Double getDiscountApplied() { return discountApplied; }
    public void setDiscountApplied(Double discountApplied) { 
        this.discountApplied = discountApplied;
        if (totalPrice != null) {
            this.finalAmount = totalPrice - discountApplied;
        }
    }
    
    public Double getFinalAmount() { return finalAmount; }
    public void setFinalAmount(Double finalAmount) { this.finalAmount = finalAmount; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public LocalDateTime getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDateTime saleDate) { this.saleDate = saleDate; }
    
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    
    public String getSalesperson() { return salesperson; }
    public void setSalesperson(String salesperson) { this.salesperson = salesperson; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    @Override
    public String toString() {
        return "Sale{" +
                "id='" + id + '\'' +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", finalAmount=" + finalAmount +
                ", saleDate=" + saleDate +
                '}';
    }
}
