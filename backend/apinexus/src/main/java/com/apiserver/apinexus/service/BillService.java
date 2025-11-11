package com.apiserver.apinexus.service;

import com.apiserver.apinexus.dto.BillGenerationRequestDTO;
import com.apiserver.apinexus.dto.BillLineItemDTO;
import com.apiserver.apinexus.dto.GeneratedBillDTO;
import com.apiserver.apinexus.model.Bill;
import com.apiserver.apinexus.model.Sale;
import com.apiserver.apinexus.model.UserSubscription;
import com.apiserver.apinexus.repository.BillRepository;
import com.apiserver.apinexus.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillService {
    
    private final BillRepository billRepository;
    private final SaleRepository saleRepository;
    
    @Transactional
    public Bill generateBill(UserSubscription subscription) {
        Bill bill = new Bill();
        bill.setUser(subscription.getUser());
        bill.setSubscription(subscription);
        bill.setBillNumber("BILL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        bill.setAmount(subscription.getAmount());
        bill.setStatus(Bill.BillStatus.PENDING);
        
        // Set billing period
        LocalDateTime now = LocalDateTime.now();
        bill.setBillingPeriodStart(now);
        
        if (subscription.getBillingCycle() == UserSubscription.BillingCycle.MONTHLY) {
            bill.setBillingPeriodEnd(now.plusMonths(1));
            bill.setDueDate(now.plusDays(7)); // 7 days to pay
        } else {
            bill.setBillingPeriodEnd(now.plusYears(1));
            bill.setDueDate(now.plusDays(14)); // 14 days to pay for yearly
        }
        
        return billRepository.save(bill);
    }
    
    public List<Bill> getUserBills(Long userId) {
        return billRepository.findByUserId(userId);
    }
    
    public List<Bill> getPendingBills() {
        return billRepository.findByStatus(Bill.BillStatus.PENDING);
    }
    
    /**
     * Generate a bill from sales data for a specific time period
     */
    public GeneratedBillDTO generateBillFromSales(BillGenerationRequestDTO request) {
        // Calculate date range based on period
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();
        String periodDescription;
        
        if (request.getPeriod() != BillGenerationRequestDTO.TimePeriod.CUSTOM) {
            LocalDate now = LocalDate.now();
            
            switch (request.getPeriod()) {
                case WEEK:
                    startDate = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                    endDate = startDate.plusDays(6);
                    periodDescription = "Week of " + startDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
                    break;
                case MONTH:
                    startDate = now.with(TemporalAdjusters.firstDayOfMonth());
                    endDate = now.with(TemporalAdjusters.lastDayOfMonth());
                    periodDescription = now.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
                    break;
                case YEAR:
                    startDate = now.with(TemporalAdjusters.firstDayOfYear());
                    endDate = now.with(TemporalAdjusters.lastDayOfYear());
                    periodDescription = now.format(DateTimeFormatter.ofPattern("yyyy"));
                    break;
                default:
                    periodDescription = "Custom Period";
            }
        } else {
            periodDescription = String.format("%s to %s", 
                startDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                endDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        }
        
        // Fetch sales within the date range
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        List<Sale> sales = saleRepository.findBySaleDateBetween(startDateTime, endDateTime);
        
        // Filter by customer if specified
        if (request.getCustomerId() != null) {
            sales = sales.stream()
                .filter(sale -> sale.getCustomerId().equals(request.getCustomerId()))
                .collect(Collectors.toList());
        } else if (request.getCustomerName() != null && !request.getCustomerName().isEmpty()) {
            String customerNameLower = request.getCustomerName().toLowerCase();
            sales = sales.stream()
                .filter(sale -> sale.getCustomerName().toLowerCase().contains(customerNameLower))
                .collect(Collectors.toList());
        }
        
        // Group sales by product to create line items
        Map<String, BillLineItemDTO> lineItemsMap = new LinkedHashMap<>();
        
        for (Sale sale : sales) {
            String productName = sale.getProductName();
            
            if (lineItemsMap.containsKey(productName)) {
                BillLineItemDTO item = lineItemsMap.get(productName);
                item.setQuantity(item.getQuantity() + sale.getQuantity());
                item.setSubtotal(item.getSubtotal() + sale.getTotalPrice());
                item.setDiscount(item.getDiscount() + sale.getDiscountApplied());
                item.setTotal(item.getTotal() + sale.getFinalAmount());
            } else {
                BillLineItemDTO item = new BillLineItemDTO();
                item.setProductName(productName);
                item.setQuantity(sale.getQuantity());
                item.setUnitPrice(sale.getUnitPrice());
                item.setSubtotal(sale.getTotalPrice());
                item.setDiscount(sale.getDiscountApplied());
                item.setTotal(sale.getFinalAmount());
                lineItemsMap.put(productName, item);
            }
        }
        
        List<BillLineItemDTO> lineItems = new ArrayList<>(lineItemsMap.values());
        
        // Calculate totals
        double subtotal = lineItems.stream().mapToDouble(BillLineItemDTO::getSubtotal).sum();
        double totalDiscount = lineItems.stream().mapToDouble(BillLineItemDTO::getDiscount).sum();
        double taxableAmount = subtotal - totalDiscount;
        double taxRate = 0.10; // 10% tax
        double taxAmount = taxableAmount * taxRate;
        double grandTotal = taxableAmount + taxAmount;
        
        // Statistics
        int totalTransactions = sales.size();
        int totalItemsSold = sales.stream().mapToInt(Sale::getQuantity).sum();
        
        // Find most common payment method
        Map<String, Long> paymentMethodCounts = sales.stream()
            .collect(Collectors.groupingBy(Sale::getPaymentMethod, Collectors.counting()));
        String mostCommonPaymentMethod = paymentMethodCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
        
        // Get customer info (use first sale's customer if filtered by customer)
        String customerName = null;
        Long customerId = null;
        if (!sales.isEmpty()) {
            Sale firstSale = sales.get(0);
            if (request.getCustomerId() != null || request.getCustomerName() != null) {
                customerName = firstSale.getCustomerName();
                customerId = firstSale.getCustomerId();
            }
        }
        
        // Generate bill
        GeneratedBillDTO bill = new GeneratedBillDTO();
        bill.setBillNumber("SB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        bill.setGeneratedDate(LocalDateTime.now());
        bill.setPeriod(periodDescription);
        bill.setPeriodStartDate(startDate);
        bill.setPeriodEndDate(endDate);
        bill.setCustomerName(customerName);
        bill.setCustomerId(customerId);
        bill.setLineItems(lineItems);
        bill.setSubtotal(subtotal);
        bill.setTotalDiscount(totalDiscount);
        bill.setTaxableAmount(taxableAmount);
        bill.setTaxRate(taxRate);
        bill.setTaxAmount(taxAmount);
        bill.setGrandTotal(grandTotal);
        bill.setTotalTransactions(totalTransactions);
        bill.setTotalItemsSold(totalItemsSold);
        bill.setPaymentMethod(mostCommonPaymentMethod);
        
        return bill;
    }
}
