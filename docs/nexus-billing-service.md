# NexusPay Billing Service

The core billing engine that manages subscriptions, plans, invoices, and payment processing logic.

## Service Structure

```
nexus-billing-service/
├── src/
│   └── main/
│       ├── java/com/nexuspay/billing/
│       │   ├── BillingServiceApplication.java
│       │   ├── controller/
│       │   │   ├── SubscriptionController.java
│       │   │   ├── InvoiceController.java
│       │   │   ├── PlanController.java
│       │   │   └── PaymentController.java
│       │   ├── service/
│       │   │   ├── SubscriptionService.java
│       │   │   ├── InvoiceService.java
│       │   │   ├── PricingEngine.java
│       │   │   ├── TaxCalculationService.java
│       │   │   └── PaymentProcessorService.java
│       │   ├── model/
│       │   │   ├── Subscription.java
│       │   │   ├── Invoice.java
│       │   │   ├── Plan.java
│       │   │   ├── Payment.java
│       │   │   └── LineItem.java
│       │   ├── repository/
│       │   │   ├── SubscriptionRepository.java
│       │   │   ├── InvoiceRepository.java
│       │   │   ├── PlanRepository.java
│       │   │   └── PaymentRepository.java
│       │   ├── integration/
│       │   │   ├── StripeClient.java
│       │   │   ├── TaxJarClient.java
│       │   │   └── WebhookService.java
│       │   ├── config/
│       │   │   ├── DatabaseConfig.java
│       │   │   ├── SecurityConfig.java
│       │   │   └── KafkaConfig.java
│       │   └── exception/
│       │       ├── BillingException.java
│       │       └── PaymentException.java
│       └── resources/
│           ├── application.yml
│           ├── db/migration/
│           │   ├── V1__initial_schema.sql
│           │   ├── V2__add_proration.sql
│           │   └── V3__add_tax_tables.sql
│           └── static/
│               └── invoice-templates/
│                   └── default-invoice.html
├── Dockerfile
└── pom.xml
```

## Billing Service POM (nexus-billing-service/pom.xml)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.nexuspay</groupId>
        <artifactId>nexus-pay-parent</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <relativePath>../pom.xml</relativePath>
    </parent>
    
    <artifactId>nexus-billing-service</artifactId>
    <name>NexusPay Billing Service</name>
    <description>Core billing engine for subscription management and invoice processing</description>
    
    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        
        <!-- Database -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
        </dependency>
        
        <!-- Flyway for database migrations -->
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>
        
        <!-- Kafka for events -->
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>
        
        <!-- HTTP clients for integrations -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>
        
        <!-- Stripe SDK -->
        <dependency>
            <groupId>com.stripe</groupId>
            <artifactId>stripe-java</artifactId>
            <version>24.16.0</version>
        </dependency>
        
        <!-- JSON processing -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
        
        <dependency>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jsr310</artifactId>
        </dependency>
        
        <!-- PDF generation for invoices -->
        <dependency>
            <groupId>com.itextpdf</groupId>
            <artifactId>itext7-core</artifactId>
            <version>7.2.5</version>
        </dependency>
        
        <!-- Template engine for invoice generation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        
        <!-- BigDecimal math operations -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-math3</artifactId>
            <version>3.6.1</version>
        </dependency>
        
        <!-- Scheduling -->
        <dependency>
            <groupId>net.javacrumbs.shedlock</groupId>
            <artifactId>shedlock-spring</artifactId>
            <version>5.10.0</version>
        </dependency>
        
        <dependency>
            <groupId>net.javacrumbs.shedlock</groupId>
            <artifactId>shedlock-provider-jdbc-template</artifactId>
            <version>5.10.0</version>
        </dependency>
        
        <!-- Metrics -->
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>
        
        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>postgresql</artifactId>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>kafka</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            
            <plugin>
                <groupId>org.flywaydb</groupId>
                <artifactId>flyway-maven-plugin</artifactId>
                <version>9.22.3</version>
            </plugin>
        </plugins>
    </build>
</project>
```

## Main Application (BillingServiceApplication.java)

```java
package com.nexuspay.billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableKafka
@EnableScheduling
@EnableTransactionManagement
public class BillingServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(BillingServiceApplication.class, args);
    }
}
```

## Core Domain Models

### Subscription Entity (model/Subscription.java)

```java
package com.nexuspay.billing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subscriptions", schema = "billing")
@NamedQueries({
    @NamedQuery(
        name = "Subscription.findActiveByTenantId",
        query = "SELECT s FROM Subscription s WHERE s.tenantId = :tenantId AND s.status = 'ACTIVE'"
    ),
    @NamedQuery(
        name = "Subscription.findDueForBilling",
        query = "SELECT s FROM Subscription s WHERE s.nextBillingDate <= :date AND s.status = 'ACTIVE'"
    )
})
public class Subscription {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "tenant_id")
    private String tenantId;
    
    @NotNull
    @Column(name = "customer_id")
    private String customerId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;
    
    @Column(name = "current_period_start")
    private LocalDateTime currentPeriodStart;
    
    @Column(name = "current_period_end")  
    private LocalDateTime currentPeriodEnd;
    
    @Column(name = "next_billing_date")
    private LocalDateTime nextBillingDate;
    
    @Column(name = "base_amount", precision = 19, scale = 4)
    private BigDecimal baseAmount;
    
    @Column(name = "quantity")
    private Integer quantity = 1;
    
    @Column(name = "trial_end")
    private LocalDateTime trialEnd;
    
    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;
    
    @Column(name = "ended_at")
    private LocalDateTime endedAt;
    
    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Invoice> invoices = new ArrayList<>();
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors, getters, and setters
    public Subscription() {}
    
    public Subscription(String tenantId, String customerId, Plan plan) {
        this.tenantId = tenantId;
        this.customerId = customerId;
        this.plan = plan;
        this.baseAmount = plan.getPrice();
        this.currentPeriodStart = LocalDateTime.now();
        this.currentPeriodEnd = calculatePeriodEnd(plan.getInterval());
        this.nextBillingDate = this.currentPeriodEnd;
    }
    
    private LocalDateTime calculatePeriodEnd(String interval) {
        LocalDateTime start = this.currentPeriodStart;
        return switch (interval.toLowerCase()) {
            case "day" -> start.plusDays(1);
            case "week" -> start.plusWeeks(1);
            case "month" -> start.plusMonths(1);
            case "quarter" -> start.plusMonths(3);
            case "year" -> start.plusYears(1);
            default -> start.plusMonths(1);
        };
    }
    
    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE;
    }
    
    public boolean isInTrial() {
        return trialEnd != null && LocalDateTime.now().isBefore(trialEnd);
    }
    
    // Getters and setters...
}

enum SubscriptionStatus {
    ACTIVE, CANCELED, PAST_DUE, TRIALING, INCOMPLETE, INCOMPLETE_EXPIRED
}
```

### Invoice Entity (model/Invoice.java)

```java
package com.nexuspay.billing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices", schema = "billing")
public class Invoice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "invoice_number", unique = true)
    private String invoiceNumber;
    
    @NotNull
    @Column(name = "tenant_id")
    private String tenantId;
    
    @NotNull
    @Column(name = "customer_id") 
    private String customerId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InvoiceStatus status = InvoiceStatus.DRAFT;
    
    @Column(name = "subtotal", precision = 19, scale = 4)
    private BigDecimal subtotal = BigDecimal.ZERO;
    
    @Column(name = "tax_amount", precision = 19, scale = 4)
    private BigDecimal taxAmount = BigDecimal.ZERO;
    
    @Column(name = "total_amount", precision = 19, scale = 4)
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    @Column(name = "amount_paid", precision = 19, scale = 4)
    private BigDecimal amountPaid = BigDecimal.ZERO;
    
    @Column(name = "amount_due", precision = 19, scale = 4)
    private BigDecimal amountDue = BigDecimal.ZERO;
    
    @Column(name = "currency", length = 3)
    private String currency = "USD";
    
    @Column(name = "period_start")
    private LocalDateTime periodStart;
    
    @Column(name = "period_end")
    private LocalDateTime periodEnd;
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @Column(name = "paid_at")
    private LocalDateTime paidAt;
    
    @Column(name = "finalized_at")
    private LocalDateTime finalizedAt;
    
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LineItem> lineItems = new ArrayList<>();
    
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructor
    public Invoice() {}
    
    public Invoice(Subscription subscription) {
        this.tenantId = subscription.getTenantId();
        this.customerId = subscription.getCustomerId();
        this.subscription = subscription;
        this.periodStart = subscription.getCurrentPeriodStart();
        this.periodEnd = subscription.getCurrentPeriodEnd();
        this.dueDate = LocalDateTime.now().plusDays(30); // 30 days payment terms
        this.generateInvoiceNumber();
    }
    
    private void generateInvoiceNumber() {
        // Generate unique invoice number: INV-YYYY-MM-XXXXXXX
        String timestamp = LocalDateTime.now().toString().replaceAll("[:-]", "").substring(0, 15);
        this.invoiceNumber = "INV-" + timestamp + "-" + Math.abs(hashCode() % 10000);
    }
    
    public void addLineItem(LineItem lineItem) {
        lineItems.add(lineItem);
        lineItem.setInvoice(this);
        recalculateTotals();
    }
    
    public void recalculateTotals() {
        this.subtotal = lineItems.stream()
            .map(LineItem::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.totalAmount = subtotal.add(taxAmount);
        this.amountDue = totalAmount.subtract(amountPaid);
    }
    
    public boolean isPaid() {
        return status == InvoiceStatus.PAID;
    }
    
    public void markAsPaid() {
        this.status = InvoiceStatus.PAID;
        this.paidAt = LocalDateTime.now();
        this.amountPaid = this.totalAmount;
        this.amountDue = BigDecimal.ZERO;
    }
    
    // Getters and setters...
}

enum InvoiceStatus {
    DRAFT, OPEN, PAID, PAST_DUE, CANCELED, VOID
}
```

## Billing Service Implementation (service/SubscriptionService.java)

```java
package com.nexuspay.billing.service;

import com.nexuspay.billing.model.*;
import com.nexuspay.billing.repository.SubscriptionRepository;
import com.nexuspay.billing.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SubscriptionService {
    
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    
    @Autowired
    private InvoiceRepository invoiceRepository;
    
    @Autowired
    private InvoiceService invoiceService;
    
    @Autowired
    private PricingEngine pricingEngine;
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    public Subscription createSubscription(String tenantId, String customerId, Plan plan) {
        Subscription subscription = new Subscription(tenantId, customerId, plan);
        
        // Apply trial if plan has trial period
        if (plan.getTrialPeriodDays() != null && plan.getTrialPeriodDays() > 0) {
            subscription.setTrialEnd(LocalDateTime.now().plusDays(plan.getTrialPeriodDays()));
            subscription.setStatus(SubscriptionStatus.TRIALING);
        }
        
        subscription = subscriptionRepository.save(subscription);
        
        // Publish subscription created event
        publishEvent("subscription.created", subscription);
        
        return subscription;
    }
    
    public Subscription updateSubscription(Long subscriptionId, Plan newPlan) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
            .orElseThrow(() -> new RuntimeException("Subscription not found"));
        
        // Calculate prorated amount for plan change
        BigDecimal proratedAmount = pricingEngine.calculateProration(
            subscription, newPlan, LocalDateTime.now()
        );
        
        // Create proration invoice if needed
        if (proratedAmount.compareTo(BigDecimal.ZERO) > 0) {
            Invoice proratedInvoice = invoiceService.createProrationInvoice(
                subscription, proratedAmount, "Plan upgrade proration"
            );
            invoiceService.finalizeInvoice(proratedInvoice.getId());
        }
        
        // Update subscription
        Plan oldPlan = subscription.getPlan();
        subscription.setPlan(newPlan);
        subscription.setBaseAmount(newPlan.getPrice());
        
        subscription = subscriptionRepository.save(subscription);
        
        // Publish plan change event
        publishEvent("subscription.plan_changed", subscription);
        
        return subscription;
    }
    
    public void cancelSubscription(Long subscriptionId, LocalDateTime cancelAt) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
            .orElseThrow(() -> new RuntimeException("Subscription not found"));
        
        if (cancelAt == null || cancelAt.isBefore(LocalDateTime.now())) {
            // Immediate cancellation
            subscription.setStatus(SubscriptionStatus.CANCELED);
            subscription.setCanceledAt(LocalDateTime.now());
            subscription.setEndedAt(LocalDateTime.now());
        } else {
            // Schedule cancellation
            subscription.setCanceledAt(cancelAt);
            subscription.setEndedAt(subscription.getCurrentPeriodEnd());
        }
        
        subscriptionRepository.save(subscription);
        publishEvent("subscription.canceled", subscription);
    }
    
    @Scheduled(fixedDelay = 300000) // Run every 5 minutes
    public void processBillingCycle() {
        LocalDateTime now = LocalDateTime.now();
        List<Subscription> dueSubscriptions = subscriptionRepository
            .findDueForBilling(now);
        
        for (Subscription subscription : dueSubscriptions) {
            try {
                processSubscriptionBilling(subscription);
            } catch (Exception e) {
                // Log error and continue with next subscription
                publishEvent("subscription.billing_failed", subscription);
            }
        }
    }
    
    private void processSubscriptionBilling(Subscription subscription) {
        // Skip if subscription is in trial
        if (subscription.isInTrial()) {
            return;
        }
        
        // Create invoice for the period
        Invoice invoice = new Invoice(subscription);
        
        // Add base subscription line item
        LineItem baseItem = new LineItem(
            subscription.getPlan().getName(),
            subscription.getQuantity(),
            subscription.getBaseAmount(),
            "Subscription for " + formatPeriod(subscription.getCurrentPeriodStart(), subscription.getCurrentPeriodEnd())
        );
        invoice.addLineItem(baseItem);
        
        // Add usage-based line items from metering service
        List<LineItem> usageItems = pricingEngine.calculateUsageCharges(
            subscription, subscription.getCurrentPeriodStart(), subscription.getCurrentPeriodEnd()
        );
        usageItems.forEach(invoice::addLineItem);
        
        // Save and finalize invoice
        invoice = invoiceRepository.save(invoice);
        invoiceService.finalizeInvoice(invoice.getId());
        
        // Update subscription billing cycle
        subscription.setCurrentPeriodStart(subscription.getCurrentPeriodEnd());
        subscription.setCurrentPeriodEnd(calculateNextPeriodEnd(subscription));
        subscription.setNextBillingDate(subscription.getCurrentPeriodEnd());
        
        subscriptionRepository.save(subscription);
        
        // Publish billing event
        publishEvent("subscription.billed", subscription);
    }
    
    private void publishEvent(String eventType, Object data) {
        kafkaTemplate.send("billing-events", eventType, data);
    }
    
    // Helper methods...
}
```

This billing service provides:

1. **Subscription lifecycle management** - create, update, cancel subscriptions
2. **Automated billing cycles** - scheduled processing of recurring charges
3. **Proration handling** - calculate prorated amounts for plan changes
4. **Usage-based billing** - integrate with metering service for usage charges
5. **Event publishing** - emit events for other services to consume
6. **Multi-tenant support** - all data isolated by tenant ID