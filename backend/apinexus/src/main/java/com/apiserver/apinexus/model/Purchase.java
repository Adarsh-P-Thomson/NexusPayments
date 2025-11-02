package com.apiserver.apinexus.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MongoDB Document for storing purchase transactions
 * Collection: purchases
 */
@Document(collection = "purchases")
public class Purchase {
    
    @Id
    private String id;
    
    @Field("user_id")
    private Long userId;
    
    @Field("username")
    private String username;
    
    @Field("is_premium_user")
    private Boolean isPremiumUser;
    
    @Field("items")
    private List<PurchaseItem> items;
    
    @Field("subtotal")
    private Double subtotal;
    
    @Field("discount_amount")
    private Double discountAmount;
    
    @Field("discount_percentage")
    private Double discountPercentage;
    
    @Field("final_bill_amount")
    private Double finalBillAmount;
    
    @Field("payment_method")
    private String paymentMethod;
    
    @Field("purchase_date")
    private LocalDateTime purchaseDate;
    
    @Field("status")
    private String status; // COMPLETED, PENDING, CANCELLED, REFUNDED
    
    // Inner class for purchased items
    public static class PurchaseItem {
        @Field("item_id")
        private Long itemId;
        
        @Field("item_name")
        private String itemName;
        
        @Field("category")
        private String category;
        
        @Field("quantity")
        private Integer quantity;
        
        @Field("unit_price")
        private Double unitPrice;
        
        @Field("total_price")
        private Double totalPrice;
        
        // Constructors
        public PurchaseItem() {}
        
        public PurchaseItem(Long itemId, String itemName, String category, Integer quantity, Double unitPrice) {
            this.itemId = itemId;
            this.itemName = itemName;
            this.category = category;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.totalPrice = quantity * unitPrice;
        }
        
        // Getters and Setters
        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }
        
        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { 
            this.quantity = quantity;
            if (unitPrice != null) {
                this.totalPrice = quantity * unitPrice;
            }
        }
        
        public Double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(Double unitPrice) { 
            this.unitPrice = unitPrice;
            if (quantity != null) {
                this.totalPrice = quantity * unitPrice;
            }
        }
        
        public Double getTotalPrice() { return totalPrice; }
        public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    }
    
    // Constructors
    public Purchase() {}
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public Boolean getIsPremiumUser() { return isPremiumUser; }
    public void setIsPremiumUser(Boolean isPremiumUser) { this.isPremiumUser = isPremiumUser; }
    
    public List<PurchaseItem> getItems() { return items; }
    public void setItems(List<PurchaseItem> items) { this.items = items; }
    
    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
    
    public Double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Double discountAmount) { this.discountAmount = discountAmount; }
    
    public Double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(Double discountPercentage) { this.discountPercentage = discountPercentage; }
    
    public Double getFinalBillAmount() { return finalBillAmount; }
    public void setFinalBillAmount(Double finalBillAmount) { this.finalBillAmount = finalBillAmount; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    @Override
    public String toString() {
        return "Purchase{" +
                "id='" + id + '\'' +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", isPremiumUser=" + isPremiumUser +
                ", items=" + items.size() +
                ", subtotal=" + subtotal +
                ", discountAmount=" + discountAmount +
                ", finalBillAmount=" + finalBillAmount +
                ", purchaseDate=" + purchaseDate +
                ", status='" + status + '\'' +
                '}';
    }
}
