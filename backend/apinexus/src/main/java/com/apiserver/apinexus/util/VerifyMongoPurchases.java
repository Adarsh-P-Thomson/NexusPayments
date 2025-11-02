package com.apiserver.apinexus.util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility to verify and display MongoDB purchase data
 */
public class VerifyMongoPurchases {
    
    private static final String MONGO_URI = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "nexuspay";
    private static final String COLLECTION_NAME = "purchases";
    
    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create(MONGO_URI)) {
            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
            
            long totalPurchases = collection.countDocuments();
            
            System.out.println("\n================================================================================");
            System.out.println("MONGODB PURCHASE TRANSACTIONS - DETAILED VIEW");
            System.out.println("================================================================================");
            System.out.println("Total Purchases: " + totalPurchases);
            System.out.println("================================================================================\n");
            
            // Display all purchases
            collection.find().forEach(doc -> {
                System.out.println("┌─────────────────────────────────────────────────────────────────────────────");
                System.out.println("│ 📦 PURCHASE ID: " + doc.get("_id"));
                System.out.println("├─────────────────────────────────────────────────────────────────────────────");
                System.out.println("│ 👤 Customer Information:");
                System.out.println("│    • User ID: " + doc.get("user_id"));
                System.out.println("│    • Username: " + doc.get("username"));
                System.out.println("│    • Premium User: " + (doc.getBoolean("is_premium_user") ? "✓ YES" : "✗ NO"));
                System.out.println("│");
                
                List<Document> items = (List<Document>) doc.get("items");
                System.out.println("│ 🛒 Items Purchased (" + items.size() + " items):");
                int itemNum = 1;
                for (Document item : items) {
                    System.out.println("│    " + itemNum + ". " + item.getString("item_name"));
                    System.out.println("│       Category: " + item.getString("category"));
                    System.out.println("│       Quantity: " + item.getInteger("quantity"));
                    System.out.println("│       Unit Price: $" + item.getDouble("unit_price"));
                    System.out.println("│       Total: $" + item.getDouble("total_price"));
                    if (itemNum < items.size()) {
                        System.out.println("│");
                    }
                    itemNum++;
                }
                
                System.out.println("│");
                System.out.println("│ 💰 Bill Summary:");
                System.out.println("│    • Subtotal: $" + doc.getDouble("subtotal"));
                System.out.println("│    • Discount (" + doc.getDouble("discount_percentage") + "%): -$" + doc.getDouble("discount_amount"));
                System.out.println("│    • FINAL BILL: $" + doc.getDouble("final_bill_amount"));
                System.out.println("│");
                System.out.println("│ 💳 Payment Details:");
                System.out.println("│    • Payment Method: " + doc.getString("payment_method"));
                System.out.println("│    • Status: " + doc.getString("status"));
                System.out.println("│    • Purchase Date: " + doc.get("purchase_date"));
                System.out.println("└─────────────────────────────────────────────────────────────────────────────\n");
            });
            
            // Summary Statistics
            System.out.println("================================================================================");
            System.out.println("📊 PURCHASE STATISTICS");
            System.out.println("================================================================================");
            
            // Count by user type
            long premiumPurchases = collection.countDocuments(new Document("is_premium_user", true));
            long normalPurchases = collection.countDocuments(new Document("is_premium_user", false));
            
            System.out.println("Purchases by User Type:");
            System.out.println("  • Premium Users: " + premiumPurchases);
            System.out.println("  • Normal Users: " + normalPurchases);
            
            // Count by status
            long completed = collection.countDocuments(new Document("status", "COMPLETED"));
            long pending = collection.countDocuments(new Document("status", "PENDING"));
            
            System.out.println("\nPurchases by Status:");
            System.out.println("  • Completed: " + completed);
            System.out.println("  • Pending: " + pending);
            
            // Calculate total revenue
            double totalRevenue = 0;
            double totalDiscounts = 0;
            
            for (Document doc : collection.find()) {
                totalRevenue += doc.getDouble("final_bill_amount");
                totalDiscounts += doc.getDouble("discount_amount");
            }
            
            System.out.println("\nFinancial Summary:");
            System.out.println("  • Total Revenue: $" + String.format("%.2f", totalRevenue));
            System.out.println("  • Total Discounts Given: $" + String.format("%.2f", totalDiscounts));
            System.out.println("  • Potential Revenue (without discounts): $" + String.format("%.2f", totalRevenue + totalDiscounts));
            
            System.out.println("\n================================================================================");
            System.out.println("✓ Verification completed!");
            System.out.println("================================================================================\n");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
