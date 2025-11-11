package com.apiserver.apinexus.service;

import com.apiserver.apinexus.dto.ProductPerformanceDTO;
import com.apiserver.apinexus.dto.SuggestionDTO;
import com.apiserver.apinexus.model.Sale;
import com.apiserver.apinexus.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SuggestionService {
    
    @Autowired
    private SaleRepository saleRepository;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * Get all suggestions across categories
     */
    public List<SuggestionDTO> getAllSuggestions() {
        List<SuggestionDTO> suggestions = new ArrayList<>();
        suggestions.addAll(getInventorySuggestions());
        suggestions.addAll(getPricingSuggestions());
        suggestions.addAll(getMarketingSuggestions());
        suggestions.addAll(getRegionalSuggestions());
        suggestions.addAll(getProductBundlingSuggestions());
        
        // Sort by priority and impact
        suggestions.sort((a, b) -> {
            int priorityCompare = comparePriority(a.getPriority(), b.getPriority());
            if (priorityCompare != 0) return priorityCompare;
            return Double.compare(b.getImpactScore(), a.getImpactScore());
        });
        
        return suggestions;
    }
    
    /**
     * Inventory optimization suggestions
     */
    public List<SuggestionDTO> getInventorySuggestions() {
        List<SuggestionDTO> suggestions = new ArrayList<>();
        List<ProductPerformanceDTO> performances = analyzeProductPerformance();
        
        // Get current stock levels from database
        Map<Long, Integer> stockLevels = getCurrentStockLevels();
        
        // Top performers needing restock
        for (ProductPerformanceDTO perf : performances) {
            if ("TOP_PERFORMER".equals(perf.getPerformanceStatus())) {
                Integer currentStock = stockLevels.getOrDefault(perf.getProductId(), 0);
                double dailyVelocity = perf.getVelocityScore();
                int daysUntilStockout = currentStock > 0 ? (int)(currentStock / dailyVelocity) : 0;
                
                if (daysUntilStockout < 30) {
                    int suggestedRestock = (int)(dailyVelocity * 60); // 60 days supply
                    suggestions.add(new SuggestionDTO(
                        "INVENTORY",
                        daysUntilStockout < 10 ? "HIGH" : "MEDIUM",
                        "Restock High-Demand Product: " + perf.getProductName(),
                        String.format("Product selling at %.1f units/day. Current stock (%d) will deplete in %d days.",
                            dailyVelocity, currentStock, daysUntilStockout),
                        String.format("Order %d units to maintain 60-day supply", suggestedRestock),
                        daysUntilStockout < 10 ? 90.0 : 70.0,
                        "Stock Days Remaining",
                        (double) daysUntilStockout,
                        60.0
                    ));
                }
            }
        }
        
        // Slow-moving inventory
        for (ProductPerformanceDTO perf : performances) {
            if ("SLOW_MOVING".equals(perf.getPerformanceStatus()) || "STAGNANT".equals(perf.getPerformanceStatus())) {
                Integer currentStock = stockLevels.getOrDefault(perf.getProductId(), 0);
                if (currentStock > 20) {
                    suggestions.add(new SuggestionDTO(
                        "INVENTORY",
                        "MEDIUM",
                        "Reduce Slow-Moving Inventory: " + perf.getProductName(),
                        String.format("Product has %d units in stock but sells only %.1f units/day. Last sale %d days ago.",
                            currentStock, perf.getVelocityScore(), perf.getDaysSinceLastSale()),
                        "Run clearance promotion at 20-30% discount or bundle with popular items",
                        60.0,
                        "Excess Stock Value",
                        currentStock * perf.getAvgUnitPrice(),
                        10 * perf.getAvgUnitPrice()
                    ));
                }
            }
        }
        
        return suggestions;
    }
    
    /**
     * Pricing optimization suggestions
     */
    public List<SuggestionDTO> getPricingSuggestions() {
        List<SuggestionDTO> suggestions = new ArrayList<>();
        List<Sale> allSales = saleRepository.findAll();
        
        // Analyze price elasticity opportunities
        Map<String, List<Sale>> salesByCategory = allSales.stream()
            .collect(Collectors.groupingBy(Sale::getCategory));
        
        for (Map.Entry<String, List<Sale>> entry : salesByCategory.entrySet()) {
            String category = entry.getKey();
            List<Sale> categorySales = entry.getValue();
            
            // Calculate average discount given
            double avgDiscount = categorySales.stream()
                .mapToDouble(s -> s.getDiscountApplied() != null ? s.getDiscountApplied() : 0.0)
                .average().orElse(0.0);
            
            double totalRevenue = categorySales.stream()
                .mapToDouble(Sale::getFinalAmount)
                .sum();
            
            // If high discounts given, suggest premium pricing strategy
            if (avgDiscount / totalRevenue > 0.05) {
                suggestions.add(new SuggestionDTO(
                    "PRICING",
                    "MEDIUM",
                    "Optimize Discounting Strategy: " + category,
                    String.format("Category has $%.2f average discount per sale (%.1f%% of revenue). Consider value-based pricing.",
                        avgDiscount / categorySales.size(), (avgDiscount / totalRevenue) * 100),
                    "Test 10-15% price increase for premium customers or introduce tiered pricing",
                    65.0,
                    "Discount Rate",
                    (avgDiscount / totalRevenue) * 100,
                    3.0
                ));
            }
        }
        
        // Dynamic pricing opportunities
        List<ProductPerformanceDTO> topPerformers = analyzeProductPerformance().stream()
            .filter(p -> "TOP_PERFORMER".equals(p.getPerformanceStatus()))
            .limit(5)
            .collect(Collectors.toList());
        
        for (ProductPerformanceDTO perf : topPerformers) {
            if (perf.getAvgOrderValue() > 100) {
                suggestions.add(new SuggestionDTO(
                    "PRICING",
                    "LOW",
                    "Test Premium Pricing: " + perf.getProductName(),
                    String.format("High-value product ($%.2f avg order) with strong demand (%d sales). Price elasticity may be low.",
                        perf.getAvgOrderValue(), perf.getSalesCount()),
                    "A/B test 5-8% price increase for new customers",
                    55.0,
                    "Potential Revenue Uplift",
                    perf.getTotalRevenue(),
                    perf.getTotalRevenue() * 1.05
                ));
            }
        }
        
        return suggestions;
    }
    
    /**
     * Marketing and customer targeting suggestions
     */
    public List<SuggestionDTO> getMarketingSuggestions() {
        List<SuggestionDTO> suggestions = new ArrayList<>();
        List<Sale> allSales = saleRepository.findAll();
        
        // Premium customer conversion opportunity
        long totalCustomers = allSales.stream()
            .map(Sale::getCustomerId)
            .distinct()
            .count();
        
        long premiumCustomers = allSales.stream()
            .filter(s -> s.getIsPremiumCustomer() != null && s.getIsPremiumCustomer())
            .map(Sale::getCustomerId)
            .distinct()
            .count();
        
        double premiumRate = (double) premiumCustomers / totalCustomers;
        
        if (premiumRate < 0.4) {
            double potentialRevenue = allSales.stream()
                .filter(s -> s.getIsPremiumCustomer() == null || !s.getIsPremiumCustomer())
                .mapToDouble(Sale::getFinalAmount)
                .sum() * 0.15; // Assume 15% uplift from premium conversion
            
            suggestions.add(new SuggestionDTO(
                "MARKETING",
                "HIGH",
                "Increase Premium Membership Conversion",
                String.format("Only %.1f%% of customers are premium members. Premium customers generate 20%% higher lifetime value.",
                    premiumRate * 100),
                "Launch targeted campaign offering 3-month trial at 50% off to top regular customers",
                85.0,
                "Potential Annual Revenue",
                0.0,
                potentialRevenue * 4 // Quarterly estimate * 4
            ));
        }
        
        // Category cross-sell opportunities
        Map<Long, Set<String>> customerCategories = new HashMap<>();
        for (Sale sale : allSales) {
            customerCategories.computeIfAbsent(sale.getCustomerId(), k -> new HashSet<>())
                .add(sale.getCategory());
        }
        
        long singleCategoryCustomers = customerCategories.values().stream()
            .filter(cats -> cats.size() == 1)
            .count();
        
        if (singleCategoryCustomers > totalCustomers * 0.3) {
            suggestions.add(new SuggestionDTO(
                "MARKETING",
                "MEDIUM",
                "Cross-Category Promotion Campaign",
                String.format("%d customers (%.1f%%) purchase from only one category. High cross-sell potential.",
                    singleCategoryCustomers, (singleCategoryCustomers * 100.0 / totalCustomers)),
                "Create 'Complete Your Collection' email campaign with 15% discount on complementary categories",
                75.0,
                "Cross-Sell Conversion Rate",
                0.0,
                singleCategoryCustomers * 0.25 // Assume 25% conversion
            ));
        }
        
        // Re-engagement for lapsed customers
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        LocalDateTime sixtyDaysAgo = LocalDateTime.now().minusDays(60);
        
        Set<Long> recentCustomers = allSales.stream()
            .filter(s -> s.getSaleDate().isAfter(thirtyDaysAgo))
            .map(Sale::getCustomerId)
            .collect(Collectors.toSet());
        
        Set<Long> lapsedCustomers = allSales.stream()
            .filter(s -> s.getSaleDate().isBefore(thirtyDaysAgo) && s.getSaleDate().isAfter(sixtyDaysAgo))
            .map(Sale::getCustomerId)
            .filter(id -> !recentCustomers.contains(id))
            .collect(Collectors.toSet());
        
        if (lapsedCustomers.size() > 10) {
            suggestions.add(new SuggestionDTO(
                "MARKETING",
                "HIGH",
                "Win-Back Campaign for Lapsed Customers",
                String.format("%d customers haven't purchased in 30-60 days. Win-back campaigns show 15-25%% reactivation.",
                    lapsedCustomers.size()),
                "Send personalized 'We Miss You' email with 20% off their favorite category",
                80.0,
                "Lapsed Customers",
                (double) lapsedCustomers.size(),
                lapsedCustomers.size() * 0.2 // 20% reactivation target
            ));
        }
        
        return suggestions;
    }
    
    /**
     * Regional performance suggestions
     */
    public List<SuggestionDTO> getRegionalSuggestions() {
        List<SuggestionDTO> suggestions = new ArrayList<>();
        List<Sale> allSales = saleRepository.findAll();
        
        Map<String, Double> revenueByRegion = allSales.stream()
            .collect(Collectors.groupingBy(
                Sale::getRegion,
                Collectors.summingDouble(Sale::getFinalAmount)
            ));
        
        Map<String, Long> salesCountByRegion = allSales.stream()
            .collect(Collectors.groupingBy(Sale::getRegion, Collectors.counting()));
        
        double avgRegionalRevenue = revenueByRegion.values().stream()
            .mapToDouble(Double::doubleValue)
            .average().orElse(0.0);
        
        // Underperforming regions
        for (Map.Entry<String, Double> entry : revenueByRegion.entrySet()) {
            if (entry.getValue() < avgRegionalRevenue * 0.6) {
                suggestions.add(new SuggestionDTO(
                    "REGIONAL",
                    "MEDIUM",
                    "Boost Sales in " + entry.getKey(),
                    String.format("Region generating $%.2f (%.1f%% below average). %d total sales.",
                        entry.getValue(), ((avgRegionalRevenue - entry.getValue()) / avgRegionalRevenue) * 100,
                        salesCountByRegion.get(entry.getKey())),
                    "Launch region-specific promotion or increase local marketing spend by 30%",
                    70.0,
                    "Revenue Gap",
                    entry.getValue(),
                    avgRegionalRevenue
                ));
            }
        }
        
        // Top region expansion
        String topRegion = revenueByRegion.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
        
        if (topRegion != null) {
            double topRevenue = revenueByRegion.get(topRegion);
            suggestions.add(new SuggestionDTO(
                "REGIONAL",
                "LOW",
                "Expand Success in " + topRegion,
                String.format("Top performing region with $%.2f revenue. High market penetration opportunity.",
                    topRevenue),
                "Increase inventory allocation and test localized product variations",
                60.0,
                "Current Revenue",
                topRevenue,
                topRevenue * 1.25
            ));
        }
        
        return suggestions;
    }
    
    /**
     * Product bundling opportunities
     */
    public List<SuggestionDTO> getProductBundlingSuggestions() {
        List<SuggestionDTO> suggestions = new ArrayList<>();
        List<Sale> allSales = saleRepository.findAll();
        
        // Find products frequently bought by same customers
        Map<Long, List<Long>> customerProducts = new HashMap<>();
        for (Sale sale : allSales) {
            customerProducts.computeIfAbsent(sale.getCustomerId(), k -> new ArrayList<>())
                .add(sale.getProductId());
        }
        
        // Find common product pairs
        Map<String, Integer> productPairs = new HashMap<>();
        for (List<Long> products : customerProducts.values()) {
            Set<Long> uniqueProducts = new HashSet<>(products);
            List<Long> productList = new ArrayList<>(uniqueProducts);
            for (int i = 0; i < productList.size(); i++) {
                for (int j = i + 1; j < productList.size(); j++) {
                    long p1 = productList.get(i);
                    long p2 = productList.get(j);
                    String pair = p1 < p2 ? p1 + "-" + p2 : p2 + "-" + p1;
                    productPairs.merge(pair, 1, Integer::sum);
                }
            }
        }
        
        // Get top 3 pairs
        List<Map.Entry<String, Integer>> topPairs = productPairs.entrySet().stream()
            .filter(e -> e.getValue() >= 3)
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(3)
            .collect(Collectors.toList());
        
        for (Map.Entry<String, Integer> pair : topPairs) {
            String[] productIds = pair.getKey().split("-");
            suggestions.add(new SuggestionDTO(
                "PRODUCT",
                "MEDIUM",
                "Create Product Bundle",
                String.format("Products %s and %s purchased together by %d customers. Strong bundling signal.",
                    productIds[0], productIds[1], pair.getValue()),
                "Create combo bundle at 10% discount to increase average order value",
                65.0,
                "Co-Purchase Frequency",
                (double) pair.getValue(),
                pair.getValue() * 2.0 // Assume doubling with bundle
            ));
        }
        
        return suggestions;
    }
    
    /**
     * Analyze product performance metrics
     */
    private List<ProductPerformanceDTO> analyzeProductPerformance() {
        List<Sale> allSales = saleRepository.findAll();
        Map<Long, List<Sale>> salesByProduct = allSales.stream()
            .collect(Collectors.groupingBy(Sale::getProductId));
        
        LocalDateTime now = LocalDateTime.now();
        List<ProductPerformanceDTO> performances = new ArrayList<>();
        
        for (Map.Entry<Long, List<Sale>> entry : salesByProduct.entrySet()) {
            List<Sale> productSales = entry.getValue();
            if (productSales.isEmpty()) continue;
            
            Sale firstSale = productSales.stream()
                .min(Comparator.comparing(Sale::getSaleDate))
                .orElse(null);
            
            Sale lastSale = productSales.stream()
                .max(Comparator.comparing(Sale::getSaleDate))
                .orElse(null);
            
            if (firstSale == null || lastSale == null) continue;
            
            int daysSinceFirst = (int) ChronoUnit.DAYS.between(firstSale.getSaleDate(), now);
            int daysSinceLast = (int) ChronoUnit.DAYS.between(lastSale.getSaleDate(), now);
            
            int totalQty = productSales.stream().mapToInt(Sale::getQuantity).sum();
            double totalRev = productSales.stream().mapToDouble(Sale::getFinalAmount).sum();
            double avgPrice = productSales.stream().mapToDouble(Sale::getUnitPrice).average().orElse(0.0);
            
            double velocityScore = daysSinceFirst > 0 ? (double) totalQty / daysSinceFirst : 0.0;
            
            String status;
            if (velocityScore > 2.0) status = "TOP_PERFORMER";
            else if (velocityScore > 0.5) status = "STEADY";
            else if (daysSinceLast > 30) status = "STAGNANT";
            else status = "SLOW_MOVING";
            
            performances.add(new ProductPerformanceDTO(
                entry.getKey(),
                productSales.get(0).getProductName(),
                productSales.get(0).getCategory(),
                productSales.size(),
                totalQty,
                totalRev,
                totalRev / productSales.size(),
                avgPrice,
                daysSinceFirst,
                daysSinceLast,
                status,
                velocityScore
            ));
        }
        
        return performances.stream()
            .sorted(Comparator.comparing(ProductPerformanceDTO::getVelocityScore).reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * Get current stock levels from market_items table
     */
    private Map<Long, Integer> getCurrentStockLevels() {
        List<Map<String, Object>> stockData = jdbcTemplate.queryForList(
            "SELECT id, stock_quantity FROM market_items"
        );
        
        Map<Long, Integer> stockLevels = new HashMap<>();
        for (Map<String, Object> row : stockData) {
            Long id = ((Number) row.get("id")).longValue();
            Integer stock = ((Number) row.get("stock_quantity")).intValue();
            stockLevels.put(id, stock);
        }
        
        return stockLevels;
    }
    
    /**
     * Compare priority levels
     */
    private int comparePriority(String p1, String p2) {
        Map<String, Integer> priorityMap = Map.of(
            "HIGH", 1,
            "MEDIUM", 2,
            "LOW", 3
        );
        return priorityMap.getOrDefault(p1, 99) - priorityMap.getOrDefault(p2, 99);
    }
}
