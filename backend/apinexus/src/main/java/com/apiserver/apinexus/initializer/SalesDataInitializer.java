package com.apiserver.apinexus.initializer;

import com.apiserver.apinexus.model.Sale;
import com.apiserver.apinexus.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
@Order(6)
public class SalesDataInitializer implements CommandLineRunner {
    
    @Autowired
    private SaleRepository saleRepository;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    
    private static final String[] CUSTOMERS = {
        "John Smith", "Emma Wilson", "Michael Brown", "Sarah Davis", 
        "James Johnson", "Emily Martinez", "David Lee", "Jessica Garcia",
        "Robert Taylor", "Lisa Anderson", "William Thomas", "Jennifer White",
        "Richard Harris", "Patricia Clark", "Thomas Lewis", "Mary Robinson",
        "Christopher Moore", "Linda Walker", "Daniel Hall", "Barbara Allen",
        "Matthew Young", "Elizabeth King", "Anthony Wright", "Margaret Lopez",
        "Mark Hill", "Sandra Scott", "Donald Green", "Nancy Adams",
        "Steven Baker", "Karen Nelson", "Paul Carter", "Betty Mitchell",
        "Andrew Perez", "Helen Roberts", "Joshua Turner", "Dorothy Phillips",
        "Kenneth Campbell", "Donna Parker", "Kevin Evans", "Carol Edwards"
    };
    
    private static final String[] REGIONS = {
        "North America", "Europe", "Asia Pacific", "Latin America", "Middle East", 
        "Africa", "Southeast Asia", "Australia"
    };
    
    private static final String[] PAYMENT_METHODS = {
        "Credit Card", "Debit Card", "PayPal", "Bank Transfer", "Crypto", 
        "Apple Pay", "Google Pay", "Venmo"
    };
    
    private static final String[] SALESPERSONS = {
        "Alice Cooper", "Bob Dylan", "Charlie Parker", "Diana Ross", "Elvis Presley",
        "Frank Sinatra", "Grace Kelly", "Henry Ford", "Irene Dunne", "Jack Johnson",
        "Kate Hudson", "Leo DiCaprio", "Maria Carey", "Nathan Drake", "Olivia Newton"
    };
    
    @Override
    public void run(String... args) throws Exception {
        // Only initialize if no sales exist
        if (saleRepository.count() > 0) {
            System.out.println("Sales data already exists. Skipping initialization.");
            return;
        }
        
        System.out.println("Initializing sales data...");
        
        // Fetch all products from market_items
        List<Map<String, Object>> products = jdbcTemplate.queryForList(
            "SELECT id, item_name, category, normal_price FROM market_items WHERE is_available = TRUE"
        );
        
        if (products.isEmpty()) {
            System.out.println("No products found in market_items. Skipping sales initialization.");
            return;
        }
        
        System.out.println("Found " + products.size() + " products in database.");
        
        Random random = new Random();
        List<Sale> sales = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        // Generate 500 sales over the last 180 days (6 months)
        int totalSales = 500;
        int daysRange = 180;
        
        for (int i = 0; i < totalSales; i++) {
            Sale sale = new Sale();
            
            // Random product from database
            Map<String, Object> product = products.get(random.nextInt(products.size()));
            sale.setProductId(((Number) product.get("id")).longValue());
            sale.setProductName((String) product.get("item_name"));
            sale.setCategory((String) product.get("category"));
            
            // Random quantity (1-15)
            sale.setQuantity(random.nextInt(15) + 1);
            
            // Use actual product price from database
            double basePrice = ((Number) product.get("normal_price")).doubleValue();
            // Add some price variation (±10%)
            double priceVariation = basePrice * (0.9 + (random.nextDouble() * 0.2));
            sale.setUnitPrice(Math.round(priceVariation * 100.0) / 100.0);
            
            // Calculate total price
            sale.setTotalPrice(sale.getQuantity() * sale.getUnitPrice());
            
            // Random customer
            int customerIndex = random.nextInt(CUSTOMERS.length);
            sale.setCustomerId((long) (customerIndex + 1));
            sale.setCustomerName(CUSTOMERS[customerIndex]);
            
            // 35% chance of premium customer
            sale.setIsPremiumCustomer(random.nextDouble() < 0.35);
            
            // Apply discount for premium customers
            if (sale.getIsPremiumCustomer()) {
                sale.setDiscountApplied(sale.getTotalPrice() * 0.20);
            } else {
                sale.setDiscountApplied(0.0);
            }
            
            sale.setFinalAmount(sale.getTotalPrice() - sale.getDiscountApplied());
            
            // Random payment method
            sale.setPaymentMethod(PAYMENT_METHODS[random.nextInt(PAYMENT_METHODS.length)]);
            
            // Random date within last 180 days with weighted distribution
            // More recent dates have higher probability
            int daysAgo;
            if (random.nextDouble() < 0.4) {
                // 40% of sales in last 30 days
                daysAgo = random.nextInt(30);
            } else if (random.nextDouble() < 0.7) {
                // 30% of sales in 31-90 days
                daysAgo = 30 + random.nextInt(60);
            } else {
                // 30% of sales in 91-180 days
                daysAgo = 90 + random.nextInt(90);
            }
            
            sale.setSaleDate(now.minusDays(daysAgo)
                .minusHours(random.nextInt(24))
                .minusMinutes(random.nextInt(60)));
            
            // Random region
            sale.setRegion(REGIONS[random.nextInt(REGIONS.length)]);
            
            // Random salesperson
            sale.setSalesperson(SALESPERSONS[random.nextInt(SALESPERSONS.length)]);
            
            sales.add(sale);
        }
        
        // Save all sales in batches
        int batchSize = 100;
        for (int i = 0; i < sales.size(); i += batchSize) {
            int end = Math.min(i + batchSize, sales.size());
            saleRepository.saveAll(sales.subList(i, end));
            System.out.println("Saved " + end + "/" + sales.size() + " sales...");
        }
        
        // Calculate and display summary
        double totalRevenue = sales.stream()
            .mapToDouble(Sale::getFinalAmount)
            .sum();
        
        int premiumSales = (int) sales.stream()
            .filter(Sale::getIsPremiumCustomer)
            .count();
        
        double totalDiscounts = sales.stream()
            .mapToDouble(sale -> sale.getDiscountApplied() != null ? sale.getDiscountApplied() : 0.0)
            .sum();
        
        int totalQuantity = sales.stream()
            .mapToInt(Sale::getQuantity)
            .sum();
        
        System.out.println("\n=== Sales Data Initialization Complete ===");
        System.out.println("Total Sales Created: " + sales.size());
        System.out.println("Total Revenue: $" + String.format("%,.2f", totalRevenue));
        System.out.println("Total Discounts Given: $" + String.format("%,.2f", totalDiscounts));
        System.out.println("Total Units Sold: " + totalQuantity);
        System.out.println("Premium Customer Sales: " + premiumSales + " (" + String.format("%.1f", (premiumSales * 100.0 / sales.size())) + "%)");
        System.out.println("Regular Customer Sales: " + (sales.size() - premiumSales) + " (" + String.format("%.1f", ((sales.size() - premiumSales) * 100.0 / sales.size())) + "%)");
        System.out.println("Date Range: " + now.minusDays(daysRange - 1).toLocalDate() + " to " + now.toLocalDate());
        System.out.println("Products Used: " + products.size());
        System.out.println("==========================================\n");
    }
}
