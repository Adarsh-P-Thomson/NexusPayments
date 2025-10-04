# NexusPay System Architecture

## System Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                         NEXUSPAY SYSTEM                          │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                         FRONTEND (React)                         │
│                      http://localhost:5173                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌─────────────┐  ┌──────────────┐  ┌─────────────┐            │
│  │  Dashboard  │  │ Subscription │  │   Payment   │            │
│  │             │  │  Management  │  │ Management  │            │
│  │ • Analytics │  │              │  │             │            │
│  │ • Charts    │  │ • View Plans │  │ • Pay Bills │            │
│  │ • Stats     │  │ • Subscribe  │  │ • View Txns │            │
│  └─────────────┘  │ • Cancel     │  │ • Retry Pay │            │
│                   └──────────────┘  └─────────────┘            │
│                                                                   │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │              Plan Manager (Admin)                        │   │
│  │  • Create/Edit/Delete Plans                             │   │
│  │  • Set Pricing (Monthly/Yearly)                         │   │
│  └──────────────────────────────────────────────────────────┘   │
│                                                                   │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                            │ REST API Calls (Axios)
                            │
┌───────────────────────────▼─────────────────────────────────────┐
│                    BACKEND (Spring Boot)                         │
│                    http://localhost:8080/api                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                    REST Controllers                       │    │
│  ├─────────────────────────────────────────────────────────┤    │
│  │ • UserController           • PaymentController          │    │
│  │ • SubscriptionPlanController  • BillController          │    │
│  │ • SubscriptionController   • TransactionController      │    │
│  └─────────────────────────────────────────────────────────┘    │
│                              │                                    │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                    Business Services                      │    │
│  ├─────────────────────────────────────────────────────────┤    │
│  │ • SubscriptionService                                    │    │
│  │   - Create/Cancel subscriptions                          │    │
│  │   - Calculate billing cycles                             │    │
│  │                                                          │    │
│  │ • BillService                                            │    │
│  │   - Generate bills                                       │    │
│  │   - Calculate due dates                                  │    │
│  │                                                          │    │
│  │ • PaymentService                                         │    │
│  │   - Process payments (80% success, 20% fail)            │    │
│  │   - Schedule retries                                     │    │
│  │   - Handle retry logic                                   │    │
│  └─────────────────────────────────────────────────────────┘    │
│                              │                                    │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                    Data Repositories                      │    │
│  ├─────────────────────────────────────────────────────────┤    │
│  │ JPA Repositories:          MongoDB Repository:           │    │
│  │ • UserRepository           • TransactionRepository       │    │
│  │ • SubscriptionPlanRepo                                  │    │
│  │ • UserSubscriptionRepo                                  │    │
│  │ • BillRepository                                        │    │
│  └─────────────────────────────────────────────────────────┘    │
│                              │                                    │
└──────────────────────────────┼───────────────────────────────────┘
                              │
                    ┌─────────┴──────────┐
                    │                    │
                    ▼                    ▼
    ┌───────────────────────┐  ┌─────────────────────────┐
    │   PostgreSQL DB       │  │      MongoDB            │
    │   (Port 5432)         │  │   (Port 27017)          │
    ├───────────────────────┤  ├─────────────────────────┤
    │                       │  │                         │
    │ ┌─────────────────┐   │  │ ┌───────────────────┐   │
    │ │ users           │   │  │ │ transactions      │   │
    │ │ • id            │   │  │ │ • _id             │   │
    │ │ • email         │   │  │ │ • userId          │   │
    │ │ • name          │   │  │ │ • billId          │   │
    │ └─────────────────┘   │  │ │ • transactionId   │   │
    │                       │  │ │ • amount          │   │
    │ ┌─────────────────┐   │  │ │ • status          │   │
    │ │subscription_plans│   │  │ │ • paymentMethod   │   │
    │ │ • id            │   │  │ │ • scheduledRetry  │   │
    │ │ • name          │   │  │ │ • metadata        │   │
    │ │ • monthlyPrice  │   │  │ └───────────────────┘   │
    │ │ • yearlyPrice   │   │  │                         │
    │ │ • features      │   │  │ Flexible Schema for:    │
    │ └─────────────────┘   │  │ • Analytics             │
    │                       │  │ • Graph Data            │
    │ ┌─────────────────┐   │  │ • Reports               │
    │ │user_subscriptions│  │  └─────────────────────────┘
    │ │ • id            │   │
    │ │ • userId        │   │
    │ │ • planId        │   │
    │ │ • billingCycle  │   │
    │ │ • status        │   │
    │ │ • nextBilling   │   │
    │ └─────────────────┘   │
    │                       │
    │ ┌─────────────────┐   │
    │ │ bills           │   │
    │ │ • id            │   │
    │ │ • userId        │   │
    │ │ • subscriptionId│   │
    │ │ • billNumber    │   │
    │ │ • amount        │   │
    │ │ • status        │   │
    │ │ • dueDate       │   │
    │ └─────────────────┘   │
    └───────────────────────┘
```

## Data Flow Diagrams

### 1. Subscription Creation Flow

```
User                Frontend            Backend                  PostgreSQL
 │                     │                   │                         │
 │  1. Select Plan     │                   │                         │
 │─────────────────────>│                   │                         │
 │                     │                   │                         │
 │  2. Choose Monthly  │                   │                         │
 │     or Yearly       │                   │                         │
 │─────────────────────>│                   │                         │
 │                     │                   │                         │
 │  3. Click Subscribe │                   │                         │
 │─────────────────────>│                   │                         │
 │                     │                   │                         │
 │                     │  POST /api/subscriptions                    │
 │                     │───────────────────>│                         │
 │                     │                   │                         │
 │                     │                   │  Create UserSubscription│
 │                     │                   │─────────────────────────>│
 │                     │                   │                         │
 │                     │                   │  Calculate next billing │
 │                     │                   │  Set amount & status    │
 │                     │                   │                         │
 │                     │                   │<─────────────────────────│
 │                     │                   │                         │
 │                     │  Return subscription                        │
 │                     │<───────────────────│                         │
 │                     │                   │                         │
 │  Subscription Created                   │                         │
 │<─────────────────────│                   │                         │
```

### 2. Payment Processing Flow

```
User              Frontend           Backend            PostgreSQL      MongoDB
 │                   │                  │                   │             │
 │  1. View Bills    │                  │                   │             │
 │───────────────────>│                  │                   │             │
 │                   │                  │                   │             │
 │                   │  GET /api/bills/user/{id}            │             │
 │                   │──────────────────>│                   │             │
 │                   │                  │  Query bills      │             │
 │                   │                  │───────────────────>│             │
 │                   │                  │<───────────────────│             │
 │                   │<──────────────────│                   │             │
 │                   │                  │                   │             │
 │  2. Click Pay Now │                  │                   │             │
 │───────────────────>│                  │                   │             │
 │                   │                  │                   │             │
 │                   │  POST /api/payments/initiate         │             │
 │                   │──────────────────>│                   │             │
 │                   │                  │                   │             │
 │                   │                  │  Random(100)      │             │
 │                   │                  │  < 80? Success    │             │
 │                   │                  │  : Failure        │             │
 │                   │                  │                   │             │
 │                   │                  │  Update bill      │             │
 │                   │                  │  status           │             │
 │                   │                  │───────────────────>│             │
 │                   │                  │                   │             │
 │                   │                  │  Save transaction │             │
 │                   │                  │───────────────────────────────>│
 │                   │                  │                   │             │
 │                   │                  │  If failed:       │             │
 │                   │                  │  Schedule retry   │             │
 │                   │                  │  for tomorrow     │             │
 │                   │                  │                   │             │
 │                   │  Return result   │                   │             │
 │                   │<──────────────────│                   │             │
 │                   │                  │                   │             │
 │  Payment Result   │                  │                   │             │
 │<───────────────────│                  │                   │             │
 │  (Success or      │                  │                   │             │
 │   Retry tomorrow) │                  │                   │             │
```

### 3. Dashboard Analytics Flow

```
User              Frontend           Backend            MongoDB
 │                   │                  │                  │
 │  View Dashboard   │                  │                  │
 │───────────────────>│                  │                  │
 │                   │                  │                  │
 │                   │  GET /api/transactions/user/{id}    │
 │                   │──────────────────>│                  │
 │                   │                  │  Query all txns  │
 │                   │                  │──────────────────>│
 │                   │                  │                  │
 │                   │                  │  Aggregate by:   │
 │                   │                  │  • Status        │
 │                   │                  │  • Date          │
 │                   │                  │  • Amount        │
 │                   │                  │                  │
 │                   │                  │<──────────────────│
 │                   │  Return data     │                  │
 │                   │<──────────────────│                  │
 │                   │                  │                  │
 │                   │  Process data:   │                  │
 │                   │  • Pie chart     │                  │
 │                   │  • Bar chart     │                  │
 │                   │  • Statistics    │                  │
 │                   │                  │                  │
 │  Display Charts   │                  │                  │
 │<───────────────────│                  │                  │
```

## Technology Stack

### Frontend
```
┌─────────────────────────────────────┐
│         React Application           │
├─────────────────────────────────────┤
│ • React 19                          │
│ • React Router DOM (Navigation)     │
│ • Axios (HTTP Client)               │
│ • Recharts (Data Visualization)     │
│ • Tailwind CSS (Styling)            │
│ • Vite (Build Tool)                 │
└─────────────────────────────────────┘
```

### Backend
```
┌─────────────────────────────────────┐
│      Spring Boot Application        │
├─────────────────────────────────────┤
│ • Spring Boot 3.5.6                 │
│ • Spring Data JPA                   │
│ • Spring Data MongoDB               │
│ • Spring Web (REST)                 │
│ • Lombok (Boilerplate Reduction)    │
│ • Java 17                           │
└─────────────────────────────────────┘
```

### Databases
```
┌──────────────┐    ┌──────────────────┐
│  PostgreSQL  │    │     MongoDB      │
├──────────────┤    ├──────────────────┤
│ Structured   │    │ Flexible Schema  │
│ Data:        │    │ Data:            │
│ • Users      │    │ • Transactions   │
│ • Plans      │    │ • Analytics      │
│ • Subs       │    │ • Metadata       │
│ • Bills      │    └──────────────────┘
└──────────────┘
```

## Key Features & Capabilities

### ✅ Subscription Management
- Create subscriptions (Monthly/Yearly)
- Check subscription status
- Cancel subscriptions
- Automatic billing calculation

### ✅ Payment Processing
- API-based payment initiation
- 80% success / 20% failure simulation
- Automatic retry scheduling
- Manual retry capability

### ✅ Bill Generation
- Automatic bill creation
- Due date calculation
- Billing period tracking
- Status management

### ✅ Analytics & Reporting
- Transaction status distribution
- Monthly spending trends
- Bill statistics
- Real-time dashboard

### ✅ Admin Features
- Plan CRUD operations
- Pricing management
- Feature configuration
- Plan activation/deactivation

## Security Considerations (Future)

```
┌─────────────────────────────────────────────┐
│         Security Enhancements (Planned)     │
├─────────────────────────────────────────────┤
│ • JWT Authentication                        │
│ • Role-Based Access Control (RBAC)          │
│ • API Rate Limiting                         │
│ • Input Validation & Sanitization           │
│ • HTTPS/TLS Encryption                      │
│ • Password Hashing (BCrypt)                 │
│ • CSRF Protection                           │
│ • SQL Injection Prevention (Parameterized)  │
└─────────────────────────────────────────────┘
```

## Deployment Architecture (Future)

```
                    ┌─────────────┐
                    │ Load Balancer│
                    └──────┬───────┘
                           │
            ┌──────────────┼──────────────┐
            │              │              │
        ┌───▼───┐      ┌───▼───┐      ┌──▼────┐
        │ App 1 │      │ App 2 │      │ App 3 │
        └───┬───┘      └───┬───┘      └───┬───┘
            │              │              │
            └──────────────┼──────────────┘
                           │
                ┌──────────▼──────────┐
                │ Database Cluster    │
                │ • PostgreSQL Master │
                │ • PostgreSQL Replica│
                │ • MongoDB Replica   │
                └─────────────────────┘
```

## Performance Optimization

### Database Indexing
```sql
-- PostgreSQL Indexes
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_subscription_user ON user_subscriptions(user_id, status);
CREATE INDEX idx_bill_status ON bills(status);
CREATE INDEX idx_bill_due_date ON bills(due_date);
```

```javascript
// MongoDB Indexes
db.transactions.createIndex({ userId: 1, transactionDate: -1 })
db.transactions.createIndex({ billId: 1 })
db.transactions.createIndex({ status: 1 })
db.transactions.createIndex({ transactionId: 1 }, { unique: true })
```

### Caching Strategy (Future)
```
┌──────────────────────────────────┐
│         Redis Cache              │
├──────────────────────────────────┤
│ • Active subscription plans      │
│ • User session data             │
│ • Recent transactions           │
│ • API response cache            │
└──────────────────────────────────┘
```
