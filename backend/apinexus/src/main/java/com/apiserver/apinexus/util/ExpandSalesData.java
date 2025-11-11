package com.apiserver.apinexus.util;

import com.apiserver.apinexus.model.Sale;
import com.apiserver.apinexus.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
@Profile("expand-sales")
public class ExpandSalesData implements CommandLineRunner {

    @Autowired
    private SaleRepository saleRepository;

    private static final Random random = new Random();

    // Product data with varied pricing and popularity
    private static final Map<Integer, ProductInfo> PRODUCT_INFO = new HashMap<>();
    
    static class ProductInfo {
        int id;
        String name;
        String category;
        double basePrice;
        double popularity; // 0.0 to 1.0, affects frequency of sales
        
        ProductInfo(int id, String name, String category, double basePrice, double popularity) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.basePrice = basePrice;
            this.popularity = popularity;
        }
    }
    
    static {
        // Electronics - High value, LOW frequency (expensive items sell less)
        PRODUCT_INFO.put(1, new ProductInfo(1, "Laptop", "Electronics", 899.99, 0.15));  // Very expensive, rare sales
        PRODUCT_INFO.put(2, new ProductInfo(2, "Smartphone", "Electronics", 699.99, 0.25));  // Expensive, low sales
        PRODUCT_INFO.put(3, new ProductInfo(3, "Tablet", "Electronics", 499.99, 0.30));  // Moderate sales
        PRODUCT_INFO.put(4, new ProductInfo(4, "Smartwatch", "Electronics", 299.99, 0.40));  // Better frequency
        PRODUCT_INFO.put(5, new ProductInfo(5, "Wireless Earbuds", "Electronics", 149.99, 0.75));  // Popular accessory
        PRODUCT_INFO.put(6, new ProductInfo(6, "Bluetooth Speaker", "Electronics", 79.99, 0.65));  // Good sales
        PRODUCT_INFO.put(7, new ProductInfo(7, "USB-C Hub", "Electronics", 49.99, 0.55));  // Regular accessory
        PRODUCT_INFO.put(8, new ProductInfo(8, "Webcam", "Electronics", 89.99, 0.35));  // Niche product
        PRODUCT_INFO.put(9, new ProductInfo(9, "Keyboard", "Electronics", 129.99, 0.45));  // Steady sales
        PRODUCT_INFO.put(10, new ProductInfo(10, "Mouse", "Electronics", 59.99, 0.60));  // Common accessory
        
        // Food & Beverages - Low value, VERY HIGH frequency (consumables sell a lot!)
        PRODUCT_INFO.put(11, new ProductInfo(11, "Organic Coffee Beans", "Food & Beverages", 24.99, 0.95));  // TOP SELLER - everyone needs coffee!
        PRODUCT_INFO.put(12, new ProductInfo(12, "Green Tea Set", "Food & Beverages", 19.99, 0.85));  // Very popular
        PRODUCT_INFO.put(13, new ProductInfo(13, "Protein Powder", "Food & Beverages", 49.99, 0.80));  // Fitness crowd
        PRODUCT_INFO.put(14, new ProductInfo(14, "Energy Bars Box", "Food & Beverages", 29.99, 0.92));  // Huge seller - convenient snack
        PRODUCT_INFO.put(15, new ProductInfo(15, "Olive Oil", "Food & Beverages", 34.99, 0.70));  // Kitchen essential
        PRODUCT_INFO.put(16, new ProductInfo(16, "Honey Jar", "Food & Beverages", 15.99, 0.88));  // Sweet tooth favorite
        PRODUCT_INFO.put(17, new ProductInfo(17, "Dark Chocolate", "Food & Beverages", 12.99, 0.93));  // MASSIVE seller - cheap treat
        PRODUCT_INFO.put(18, new ProductInfo(18, "Almond Butter", "Food & Beverages", 18.99, 0.75));  // Health food
        PRODUCT_INFO.put(19, new ProductInfo(19, "Matcha Powder", "Food & Beverages", 27.99, 0.65));  // Trendy item
        PRODUCT_INFO.put(20, new ProductInfo(20, "Kombucha Bottles", "Food & Beverages", 22.99, 0.78));  // Healthy drink
        
        // Sports & Fitness - Moderate value, VARIED frequency (some very popular, some not)
        PRODUCT_INFO.put(21, new ProductInfo(21, "Yoga Mat", "Sports & Fitness", 39.99, 0.72));  // Popular fitness item
        PRODUCT_INFO.put(22, new ProductInfo(22, "Dumbbells Set", "Sports & Fitness", 79.99, 0.38));  // Heavy, less frequent
        PRODUCT_INFO.put(23, new ProductInfo(23, "Resistance Bands", "Sports & Fitness", 24.99, 0.82));  // Cheap, popular
        PRODUCT_INFO.put(24, new ProductInfo(24, "Jump Rope", "Sports & Fitness", 14.99, 0.90));  // Cheap, very popular
        PRODUCT_INFO.put(25, new ProductInfo(25, "Foam Roller", "Sports & Fitness", 29.99, 0.58));  // Moderate
        PRODUCT_INFO.put(26, new ProductInfo(26, "Water Bottle", "Sports & Fitness", 19.99, 0.94));  // HUGE seller - everyone needs one
        PRODUCT_INFO.put(27, new ProductInfo(27, "Gym Bag", "Sports & Fitness", 49.99, 0.48));  // Occasional purchase
        PRODUCT_INFO.put(28, new ProductInfo(28, "Fitness Tracker", "Sports & Fitness", 99.99, 0.42));  // Moderate electronics
        PRODUCT_INFO.put(29, new ProductInfo(29, "Exercise Ball", "Sports & Fitness", 34.99, 0.35));  // Niche fitness
        PRODUCT_INFO.put(30, new ProductInfo(30, "Ankle Weights", "Sports & Fitness", 27.99, 0.40));  // Specialized gear
        
        // Add more products with varied patterns
        addHealthWellnessProducts();
        addHomeOfficeProducts();
        addStationeryProducts();
    }
    
    private static void addHealthWellnessProducts() {
        PRODUCT_INFO.put(31, new ProductInfo(31, "Vitamin D Supplements", "Health & Wellness", 19.99, 0.83));  // Popular health item
        PRODUCT_INFO.put(32, new ProductInfo(32, "Multivitamin Pack", "Health & Wellness", 29.99, 0.87));  // Very popular
        PRODUCT_INFO.put(33, new ProductInfo(33, "Fish Oil Capsules", "Health & Wellness", 24.99, 0.79));  // Common supplement
        PRODUCT_INFO.put(34, new ProductInfo(34, "Probiotics", "Health & Wellness", 34.99, 0.68));  // Health conscious buyers
        PRODUCT_INFO.put(35, new ProductInfo(35, "Collagen Powder", "Health & Wellness", 44.99, 0.52));  // Trendy but pricey
        PRODUCT_INFO.put(36, new ProductInfo(36, "Essential Oils Set", "Health & Wellness", 39.99, 0.44));  // Niche product
        PRODUCT_INFO.put(37, new ProductInfo(37, "Face Masks Pack", "Health & Wellness", 15.99, 0.91));  // HUGE seller - cheap essential
        PRODUCT_INFO.put(38, new ProductInfo(38, "Hand Sanitizer", "Health & Wellness", 9.99, 0.96));  // TOP SELLER - cheap necessity
        PRODUCT_INFO.put(39, new ProductInfo(39, "Thermometer", "Health & Wellness", 29.99, 0.38));  // Occasional purchase
        PRODUCT_INFO.put(40, new ProductInfo(40, "First Aid Kit", "Health & Wellness", 49.99, 0.32));  // Rare but important
    }
    
    private static void addHomeOfficeProducts() {
        PRODUCT_INFO.put(41, new ProductInfo(41, "Desk Lamp", "Home & Office", 59.99, 0.55));  // Moderate need
        PRODUCT_INFO.put(42, new ProductInfo(42, "Office Chair", "Home & Office", 199.99, 0.22));  // Expensive, rare
        PRODUCT_INFO.put(43, new ProductInfo(43, "Standing Desk", "Home & Office", 299.99, 0.12));  // Very expensive, very rare
        PRODUCT_INFO.put(44, new ProductInfo(44, "Monitor Stand", "Home & Office", 39.99, 0.62));  // Common accessory
        PRODUCT_INFO.put(45, new ProductInfo(45, "Cable Organizer", "Home & Office", 14.99, 0.86));  // Cheap, very useful
        PRODUCT_INFO.put(46, new ProductInfo(46, "Desk Pad", "Home & Office", 24.99, 0.73));  // Popular desk item
        PRODUCT_INFO.put(47, new ProductInfo(47, "Whiteboard", "Home & Office", 49.99, 0.41));  // Occasional office purchase
        PRODUCT_INFO.put(48, new ProductInfo(48, "Paper Shredder", "Home & Office", 79.99, 0.28));  // Rare office equipment
        PRODUCT_INFO.put(49, new ProductInfo(49, "Bookshelf", "Home & Office", 129.99, 0.33));  // Occasional furniture
        PRODUCT_INFO.put(50, new ProductInfo(50, "Plant Pot", "Home & Office", 19.99, 0.77));  // Cheap decor, popular
    }
    
    private static void addStationeryProducts() {
        PRODUCT_INFO.put(51, new ProductInfo(51, "Notebook Set", "Stationery", 19.99, 0.89));  // Very popular - everyone needs
        PRODUCT_INFO.put(52, new ProductInfo(52, "Pen Collection", "Stationery", 24.99, 0.92));  // HUGE seller - constant need
        PRODUCT_INFO.put(53, new ProductInfo(53, "Sticky Notes Pack", "Stationery", 9.99, 0.97));  // TOP SELLER - cheap office staple
        PRODUCT_INFO.put(54, new ProductInfo(54, "Highlighters", "Stationery", 12.99, 0.88));  // Very popular study/work tool
        PRODUCT_INFO.put(55, new ProductInfo(55, "Binder Set", "Stationery", 29.99, 0.56));  // Moderate sales
        PRODUCT_INFO.put(56, new ProductInfo(56, "Planner 2025", "Stationery", 34.99, 0.64));  // Seasonal, but popular
        PRODUCT_INFO.put(57, new ProductInfo(57, "Stapler & Supplies", "Stationery", 15.99, 0.81));  // Office essential
        PRODUCT_INFO.put(58, new ProductInfo(58, "Tape Dispenser", "Stationery", 11.99, 0.74));  // Common office need
        PRODUCT_INFO.put(59, new ProductInfo(59, "Scissors Set", "Stationery", 17.99, 0.67));  // Moderate frequency
        PRODUCT_INFO.put(60, new ProductInfo(60, "Paper Clips Box", "Stationery", 7.99, 0.85));  // Cheap, regular purchase
    }

    private static final String[] CUSTOMER_NAMES = {
        "John Smith", "Emma Johnson", "Michael Brown", "Sophia Davis", "William Wilson",
        "Olivia Martinez", "James Anderson", "Ava Taylor", "Robert Thomas", "Isabella Moore",
        "David Jackson", "Mia White", "Joseph Harris", "Charlotte Martin", "Daniel Thompson",
        "Amelia Garcia", "Matthew Rodriguez", "Harper Lee", "Christopher Walker", "Evelyn Hall",
        "Andrew Allen", "Abigail Young", "Joshua King", "Emily Wright", "Ryan Lopez",
        "Elizabeth Hill", "Nicholas Scott", "Sofia Green", "Brandon Adams", "Avery Baker",
        "Jonathan Nelson", "Ella Carter", "Samuel Mitchell", "Scarlett Perez", "Benjamin Roberts",
        "Grace Turner", "Nathan Phillips", "Chloe Campbell", "Zachary Parker", "Lily Evans",
        "Alexander Edwards", "Zoey Collins", "Ethan Stewart", "Hannah Sanchez", "Jacob Morris",
        "Nora Rogers", "Tyler Reed", "Riley Cook", "Lucas Morgan", "Layla Bell"
    };

    private static final String[] REGIONS = {
        "North America", "Europe", "Asia Pacific", "Latin America",
        "Middle East", "Africa", "Australia", "Southeast Asia"
    };

    private static final String[] PAYMENT_METHODS = {
        "Credit Card", "Debit Card", "PayPal", "Bank Transfer",
        "Apple Pay", "Google Pay", "Cash", "Cryptocurrency"
    };

    private static final String[] SALES_PERSONS = {
        "Alice Johnson", "Bob Smith", "Carol Williams", "David Brown",
        "Eva Martinez", "Frank Davis", "Grace Miller", "Henry Wilson",
        "Ivy Moore", "Jack Taylor", "Karen Anderson", "Leo Thomas",
        "Mary Jackson", "Nathan White", "Olivia Harris"
    };

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n================================================================================");
        System.out.println("EXPANDING SALES DATA FOR VARIED ANALYTICS");
        System.out.println("================================================================================\n");

        long currentCount = saleRepository.count();
        System.out.println("Current sales count: " + currentCount);

        // Clear existing sales data for fresh start with new patterns
        System.out.println("\n🗑️  Clearing existing sales data...");
        saleRepository.deleteAll();
        System.out.println("✅ All existing sales cleared!\n");

        int totalSales = 3000; // Generate 3000 sales for good variety
        if (args.length > 0 && args[0].startsWith("--count=")) {
            totalSales = Integer.parseInt(args[0].substring(8));
        }

        System.out.println("Generating " + totalSales + " sales with REALISTIC patterns...");
        System.out.println("(Some products will sell A LOT, others very little)\n");

        List<Sale> newSales = generateVariedSales(totalSales);
        
        // Save in batches
        int batchSize = 100;
        int totalBatches = (newSales.size() + batchSize - 1) / batchSize;
        
        for (int i = 0; i < totalBatches; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, newSales.size());
            List<Sale> batch = newSales.subList(start, end);
            saleRepository.saveAll(batch);
            System.out.printf("Saved batch %d/%d (%d sales)%n", i + 1, totalBatches, batch.size());
        }

        System.out.println("\n✅ Successfully added " + newSales.size() + " new sales!");
        System.out.println("Total sales now: " + saleRepository.count());

        // Print analytics summary
        printAnalyticsSummary();

        System.out.println("\n================================================================================");
        System.exit(0);
    }

    private List<Sale> generateVariedSales(int count) {
        List<Sale> sales = new ArrayList<>();
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(365); // Full year of data

        // Create varied distribution patterns
        for (int i = 0; i < count; i++) {
            Sale sale = createSaleWithPattern(startDate, endDate, i, count);
            sales.add(sale);
        }

        return sales;
    }

    private Sale createSaleWithPattern(LocalDateTime startDate, LocalDateTime endDate, int index, int total) {
        Sale sale = new Sale();

        // Weighted date distribution - more recent sales
        double dateWeight = random.nextDouble();
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        long daysOffset;
        
        if (dateWeight < 0.3) { // 30% in last 30 days
            daysOffset = daysBetween - random.nextInt(30);
        } else if (dateWeight < 0.6) { // 30% in last 90 days
            daysOffset = daysBetween - random.nextInt(90);
        } else { // 40% distributed across the year
            daysOffset = random.nextLong(daysBetween);
        }
        
        LocalDateTime saleDate = startDate.plusDays(daysOffset)
            .plusHours(random.nextInt(24))
            .plusMinutes(random.nextInt(60));
        sale.setSaleDate(saleDate);

        // Select product based on popularity
        ProductInfo product = selectProductByPopularity();
        sale.setProductId(Long.valueOf(product.id));
        sale.setProductName(product.name);
        sale.setCategory(product.category);

        // Price variation (±10% from base price)
        double priceVariation = 0.9 + (random.nextDouble() * 0.2);
        double unitPrice = product.basePrice * priceVariation;
        sale.setUnitPrice(Math.round(unitPrice * 100.0) / 100.0);

        // Quantity - weighted towards lower quantities
        int quantity;
        double qtyRoll = random.nextDouble();
        if (qtyRoll < 0.5) quantity = 1;
        else if (qtyRoll < 0.8) quantity = 2;
        else if (qtyRoll < 0.95) quantity = 3;
        else quantity = random.nextInt(7) + 4; // 4-10
        sale.setQuantity(quantity);

        // Customer selection - some customers buy more frequently
        int customerIndex;
        if (random.nextDouble() < 0.3) { // 30% are repeat customers
            customerIndex = random.nextInt(15); // First 15 customers buy more
        } else {
            customerIndex = random.nextInt(CUSTOMER_NAMES.length);
        }
        sale.setCustomerName(CUSTOMER_NAMES[customerIndex]);
        sale.setCustomerId(Long.valueOf(customerIndex + 1));

        // Premium customer distribution - 40% are premium
        boolean isPremium = random.nextDouble() < 0.4;
        sale.setIsPremiumCustomer(isPremium);

        // Calculate totals
        double totalPrice = sale.getUnitPrice() * sale.getQuantity();
        sale.setTotalPrice(Math.round(totalPrice * 100.0) / 100.0);

        // Discount - higher for premium, varies by product
        double discountRate = 0;
        if (isPremium) {
            discountRate = 0.15 + (random.nextDouble() * 0.15); // 15-30% for premium
        } else {
            if (random.nextDouble() < 0.3) { // 30% of regular customers get discount
                discountRate = random.nextDouble() * 0.1; // 0-10%
            }
        }
        double discountApplied = sale.getTotalPrice() * discountRate;
        sale.setDiscountApplied(Math.round(discountApplied * 100.0) / 100.0);

        double finalAmount = sale.getTotalPrice() - sale.getDiscountApplied();
        sale.setFinalAmount(Math.round(finalAmount * 100.0) / 100.0);

        // Random but weighted selections for other fields
        sale.setRegion(REGIONS[weightedRandom(REGIONS.length, 0.3)]);
        sale.setPaymentMethod(PAYMENT_METHODS[weightedRandom(PAYMENT_METHODS.length, 0.4)]);
        sale.setSalesperson(SALES_PERSONS[random.nextInt(SALES_PERSONS.length)]);

        return sale;
    }

    private ProductInfo selectProductByPopularity() {
        // Weighted selection based on popularity
        double rand = random.nextDouble();
        double cumulativeProbability = 0.0;
        double totalPopularity = PRODUCT_INFO.values().stream()
            .mapToDouble(p -> p.popularity)
            .sum();

        for (ProductInfo product : PRODUCT_INFO.values()) {
            cumulativeProbability += product.popularity / totalPopularity;
            if (rand <= cumulativeProbability) {
                return product;
            }
        }

        // Fallback
        return PRODUCT_INFO.get(1);
    }

    private int weightedRandom(int max, double firstBias) {
        // Bias towards first few options
        if (random.nextDouble() < firstBias) {
            return random.nextInt(Math.min(3, max));
        }
        return random.nextInt(max);
    }

    private void printAnalyticsSummary() {
        List<Sale> allSales = saleRepository.findAll();
        
        double totalRevenue = allSales.stream().mapToDouble(Sale::getFinalAmount).sum();
        double totalDiscounts = allSales.stream().mapToDouble(Sale::getDiscountApplied).sum();
        int totalQuantity = allSales.stream().mapToInt(Sale::getQuantity).sum();
        
        long premiumSales = allSales.stream().filter(Sale::getIsPremiumCustomer).count();
        long regularSales = allSales.size() - premiumSales;
        
        System.out.println("\n📊 ANALYTICS SUMMARY:");
        System.out.println("─────────────────────────────────────────");
        System.out.printf("Total Sales Transactions: %,d%n", allSales.size());
        System.out.printf("Total Revenue: $%,.2f%n", totalRevenue);
        System.out.printf("Total Discounts: $%,.2f%n", totalDiscounts);
        System.out.printf("Total Units Sold: %,d%n", totalQuantity);
        System.out.printf("Average Order Value: $%.2f%n", totalRevenue / allSales.size());
        System.out.printf("Premium Customer Sales: %,d (%.1f%%)%n", 
            premiumSales, (premiumSales * 100.0 / allSales.size()));
        System.out.printf("Regular Customer Sales: %,d (%.1f%%)%n", 
            regularSales, (regularSales * 100.0 / allSales.size()));
        
        // Top 5 products by revenue
        Map<String, Double> productRevenue = new HashMap<>();
        for (Sale sale : allSales) {
            productRevenue.merge(sale.getProductName(), sale.getFinalAmount(), Double::sum);
        }
        
        System.out.println("\n🏆 TOP 5 PRODUCTS BY REVENUE:");
        productRevenue.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(5)
            .forEach(e -> System.out.printf("  • %s: $%,.2f%n", e.getKey(), e.getValue()));
    }
}
