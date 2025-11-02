package com.apiserver.apinexus.util;

import com.apiserver.apinexus.model.Purchase;
import com.apiserver.apinexus.model.Purchase.PurchaseItem;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Utility to populate MongoDB with sample purchase transactions
 */
public class MongoDataInitializer {
    
    private static final String MONGO_URI = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "nexuspay";
    private static final String COLLECTION_NAME = "purchases";
    
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("MONGODB PURCHASE DATA INITIALIZATION");
        System.out.println("================================================================================");
        
        try (MongoClient mongoClient = MongoClients.create(MONGO_URI)) {
            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
            
            // Clear existing data
            long deletedCount = collection.deleteMany(new Document()).getDeletedCount();
            System.out.println("✓ Cleared existing data: " + deletedCount + " documents deleted");
            
            // Create sample purchases
            List<Document> purchases = createSamplePurchases();
            
            // Insert purchases
            collection.insertMany(purchases);
            System.out.println("✓ Inserted " + purchases.size() + " purchase transactions");
            
            // Verify insertion
            long count = collection.countDocuments();
            System.out.println("\n================================================================================");
            System.out.println("VERIFICATION");
            System.out.println("================================================================================");
            System.out.println("Total purchases in collection: " + count);
            
            // Display sample purchases
            System.out.println("\n================================================================================");
            System.out.println("SAMPLE PURCHASE TRANSACTIONS");
            System.out.println("================================================================================");
            
            collection.find().limit(5).forEach(doc -> {
                System.out.println("\n📦 Purchase ID: " + doc.get("_id"));
                System.out.println("   User: " + doc.get("username") + " (ID: " + doc.get("user_id") + ")");
                System.out.println("   Premium: " + (doc.getBoolean("is_premium_user") ? "YES" : "NO"));
                System.out.println("   Items: " + ((List<?>) doc.get("items")).size());
                System.out.println("   Subtotal: $" + doc.getDouble("subtotal"));
                System.out.println("   Discount: $" + doc.getDouble("discount_amount") + 
                                 " (" + doc.getDouble("discount_percentage") + "%)");
                System.out.println("   Final Bill: $" + doc.getDouble("final_bill_amount"));
                System.out.println("   Status: " + doc.getString("status"));
                System.out.println("   Date: " + doc.get("purchase_date"));
            });
            
            System.out.println("\n================================================================================");
            System.out.println("✓ MongoDB purchase data initialization completed!");
            System.out.println("================================================================================");
            
        } catch (Exception e) {
            System.err.println("✗ Error during initialization:");
            e.printStackTrace();
        }
    }
    
    private static List<Document> createSamplePurchases() {
        List<Document> purchases = new ArrayList<>();
        
        // Purchase 1: Normal user (john_doe) - 3 items
        purchases.add(createPurchase(
            1L, "john_doe", false,
            Arrays.asList(
                createItem(1L, "Premium Coffee Beans", "Food & Beverages", 2, 24.99),
                createItem(3L, "Yoga Mat", "Sports & Fitness", 1, 34.99),
                createItem(9L, "Water Bottle", "Sports & Fitness", 1, 19.99)
            ),
            "Credit Card", "COMPLETED", LocalDateTime.now().minusDays(5)
        ));
        
        // Purchase 2: Premium user (jane_smith) - 2 items with discount
        purchases.add(createPurchase(
            2L, "jane_smith", true,
            Arrays.asList(
                createItem(2L, "Wireless Earbuds", "Electronics", 1, 59.99),
                createItem(5L, "Smart Watch", "Electronics", 1, 149.99)
            ),
            "PayPal", "COMPLETED", LocalDateTime.now().minusDays(3)
        ));
        
        // Purchase 3: Premium user (premium_user) - 4 items with discount
        purchases.add(createPurchase(
            3L, "premium_user", true,
            Arrays.asList(
                createItem(1L, "Premium Coffee Beans", "Food & Beverages", 3, 19.99),
                createItem(4L, "Organic Green Tea", "Food & Beverages", 2, 12.99),
                createItem(6L, "Protein Powder", "Health & Wellness", 1, 39.99),
                createItem(7L, "Running Shoes", "Sports & Fitness", 1, 69.99)
            ),
            "Credit Card", "COMPLETED", LocalDateTime.now().minusDays(2)
        ));
        
        // Purchase 4: Normal user (normal_user) - 1 item
        purchases.add(createPurchase(
            4L, "normal_user", false,
            Arrays.asList(
                createItem(10L, "Desk Lamp", "Home & Office", 2, 44.99)
            ),
            "Debit Card", "COMPLETED", LocalDateTime.now().minusDays(1)
        ));
        
        // Purchase 5: Premium user (jane_smith) - 3 items
        purchases.add(createPurchase(
            2L, "jane_smith", true,
            Arrays.asList(
                createItem(8L, "Notebook Set", "Stationery", 2, 24.99),
                createItem(10L, "Desk Lamp", "Home & Office", 1, 34.99),
                createItem(3L, "Yoga Mat", "Sports & Fitness", 1, 27.99)
            ),
            "Credit Card", "COMPLETED", LocalDateTime.now().minusHours(12)
        ));
        
        // Purchase 6: Normal user (john_doe) - Large order
        purchases.add(createPurchase(
            1L, "john_doe", false,
            Arrays.asList(
                createItem(5L, "Smart Watch", "Electronics", 1, 199.99),
                createItem(7L, "Running Shoes", "Sports & Fitness", 1, 89.99),
                createItem(6L, "Protein Powder", "Health & Wellness", 2, 49.99)
            ),
            "Credit Card", "COMPLETED", LocalDateTime.now().minusHours(6)
        ));
        
        // Purchase 7: Premium user (premium_user) - Single expensive item
        purchases.add(createPurchase(
            3L, "premium_user", true,
            Arrays.asList(
                createItem(5L, "Smart Watch", "Electronics", 2, 149.99)
            ),
            "PayPal", "COMPLETED", LocalDateTime.now().minusHours(3)
        ));
        
        // Purchase 8: Normal user (normal_user) - Pending order
        purchases.add(createPurchase(
            4L, "normal_user", false,
            Arrays.asList(
                createItem(2L, "Wireless Earbuds", "Electronics", 1, 79.99),
                createItem(9L, "Water Bottle", "Sports & Fitness", 2, 19.99)
            ),
            "Credit Card", "PENDING", LocalDateTime.now().minusHours(1)
        ));
        
        return purchases;
    }
    
    private static Document createPurchase(Long userId, String username, boolean isPremium,
                                          List<Document> items, String paymentMethod,
                                          String status, LocalDateTime purchaseDate) {
        // Calculate totals
        double subtotal = items.stream()
            .mapToDouble(item -> item.getDouble("total_price"))
            .sum();
        
        double discountPercentage = isPremium ? 20.0 : 0.0;
        double discountAmount = isPremium ? (subtotal * 0.20) : 0.0;
        double finalBill = subtotal - discountAmount;
        
        Document purchase = new Document();
        purchase.append("user_id", userId);
        purchase.append("username", username);
        purchase.append("is_premium_user", isPremium);
        purchase.append("items", items);
        purchase.append("subtotal", Math.round(subtotal * 100.0) / 100.0);
        purchase.append("discount_percentage", discountPercentage);
        purchase.append("discount_amount", Math.round(discountAmount * 100.0) / 100.0);
        purchase.append("final_bill_amount", Math.round(finalBill * 100.0) / 100.0);
        purchase.append("payment_method", paymentMethod);
        purchase.append("status", status);
        purchase.append("purchase_date", Date.from(purchaseDate.atZone(ZoneId.systemDefault()).toInstant()));
        
        return purchase;
    }
    
    private static Document createItem(Long itemId, String itemName, String category,
                                       int quantity, double unitPrice) {
        Document item = new Document();
        item.append("item_id", itemId);
        item.append("item_name", itemName);
        item.append("category", category);
        item.append("quantity", quantity);
        item.append("unit_price", unitPrice);
        item.append("total_price", Math.round(quantity * unitPrice * 100.0) / 100.0);
        
        return item;
    }
}
