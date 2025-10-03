# Database Schema Documentation

## Overview
The NexusPay system uses a hybrid database approach:
- **PostgreSQL**: For structured relational data (users, subscriptions, bills)
- **MongoDB**: For flexible transaction data and analytics

---

## PostgreSQL Schema

### Database: `nexuspay`

### Table: `users`
Stores user information.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique user identifier |
| email | VARCHAR(255) | UNIQUE, NOT NULL | User's email address |
| name | VARCHAR(255) | NOT NULL | User's full name |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Account creation timestamp |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Last update timestamp |

**Indexes:**
- Primary key on `id`
- Unique index on `email`

---

### Table: `subscription_plans`
Stores subscription plan definitions.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique plan identifier |
| name | VARCHAR(255) | NOT NULL | Plan name (e.g., "Basic Plan") |
| description | TEXT | | Detailed plan description |
| monthly_price | DECIMAL(10,2) | | Monthly subscription price |
| yearly_price | DECIMAL(10,2) | | Yearly subscription price |
| features | VARCHAR(255) | NOT NULL | Comma-separated list of features |
| active | BOOLEAN | NOT NULL, DEFAULT TRUE | Whether plan is active/available |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Plan creation timestamp |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Last update timestamp |

**Indexes:**
- Primary key on `id`
- Index on `active` for filtering active plans

---

### Table: `user_subscriptions`
Stores user subscription records.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique subscription identifier |
| user_id | BIGINT | FOREIGN KEY (users.id), NOT NULL | Reference to user |
| subscription_plan_id | BIGINT | FOREIGN KEY (subscription_plans.id), NOT NULL | Reference to plan |
| billing_cycle | VARCHAR(20) | NOT NULL | MONTHLY or YEARLY |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'ACTIVE' | ACTIVE, CANCELLED, EXPIRED, PENDING |
| start_date | TIMESTAMP | NOT NULL | Subscription start date |
| end_date | TIMESTAMP | | Subscription end date (for cancelled) |
| next_billing_date | TIMESTAMP | | Next billing date |
| amount | DECIMAL(10,2) | | Subscription amount |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Record creation timestamp |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Last update timestamp |

**Indexes:**
- Primary key on `id`
- Foreign key on `user_id`
- Foreign key on `subscription_plan_id`
- Index on `status` for filtering
- Composite index on `(user_id, status)` for user-specific queries

**Relationships:**
- Many-to-One with `users`
- Many-to-One with `subscription_plans`

---

### Table: `bills`
Stores billing records.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique bill identifier |
| user_id | BIGINT | FOREIGN KEY (users.id), NOT NULL | Reference to user |
| subscription_id | BIGINT | FOREIGN KEY (user_subscriptions.id), NOT NULL | Reference to subscription |
| bill_number | VARCHAR(255) | UNIQUE, NOT NULL | Unique bill number (e.g., BILL-ABC12345) |
| amount | DECIMAL(10,2) | NOT NULL | Bill amount |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'PENDING' | PENDING, PAID, FAILED, CANCELLED |
| billing_period_start | TIMESTAMP | | Billing period start date |
| billing_period_end | TIMESTAMP | | Billing period end date |
| due_date | TIMESTAMP | | Payment due date |
| paid_date | TIMESTAMP | | Payment completion date |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Bill creation timestamp |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Last update timestamp |

**Indexes:**
- Primary key on `id`
- Unique index on `bill_number`
- Foreign key on `user_id`
- Foreign key on `subscription_id`
- Index on `status` for filtering
- Index on `due_date` for payment scheduling

**Relationships:**
- Many-to-One with `users`
- Many-to-One with `user_subscriptions`

---

## MongoDB Schema

### Database: `nexuspay_transactions`

### Collection: `transactions`
Stores transaction records with flexible schema for analytics.

**Document Structure:**

```javascript
{
  _id: ObjectId("..."),                    // MongoDB auto-generated ID
  userId: 1,                               // Reference to PostgreSQL user ID
  billId: 1,                               // Reference to PostgreSQL bill ID
  transactionId: "TXN-ABC123456789",      // Unique transaction identifier
  amount: 29.99,                           // Transaction amount
  status: "SUCCESS",                       // PENDING, SUCCESS, FAILED, RETRYING, CANCELLED
  paymentMethod: "CREDIT_CARD",           // Payment method used
  transactionDate: ISODate("2024-01-01T10:30:00Z"), // Transaction timestamp
  scheduledRetryDate: ISODate("2024-01-02T10:30:00Z"), // Retry date for failed payments
  failureReason: "Payment declined",       // Reason for failure (if applicable)
  retryCount: 0,                           // Number of retry attempts
  metadata: {                              // Flexible metadata for analytics
    ipAddress: "192.168.1.1",
    userAgent: "Mozilla/5.0...",
    deviceType: "desktop",
    location: "US",
    customField1: "value1",
    customField2: "value2"
  },
  createdAt: ISODate("2024-01-01T10:30:00Z"), // Record creation timestamp
  updatedAt: ISODate("2024-01-01T10:30:00Z")  // Last update timestamp
}
```

**Indexes:**
```javascript
// User-based queries
db.transactions.createIndex({ "userId": 1, "transactionDate": -1 })

// Bill-based queries
db.transactions.createIndex({ "billId": 1 })

// Status-based queries
db.transactions.createIndex({ "status": 1 })

// Transaction ID lookup
db.transactions.createIndex({ "transactionId": 1 }, { unique: true })

// Retry scheduling
db.transactions.createIndex({ "scheduledRetryDate": 1 })

// Analytics queries
db.transactions.createIndex({ "transactionDate": -1 })
```

**Field Descriptions:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| _id | ObjectId | Yes | MongoDB auto-generated unique identifier |
| userId | Long | Yes | Reference to PostgreSQL user ID |
| billId | Long | Yes | Reference to PostgreSQL bill ID |
| transactionId | String | Yes | Unique transaction identifier (TXN-...) |
| amount | Decimal128/Number | Yes | Transaction amount |
| status | String | Yes | Transaction status enum |
| paymentMethod | String | No | Payment method (CREDIT_CARD, DEBIT_CARD, etc.) |
| transactionDate | Date | Yes | When the transaction occurred |
| scheduledRetryDate | Date | No | Scheduled retry date for failed payments |
| failureReason | String | No | Reason for transaction failure |
| retryCount | Integer | No | Number of retry attempts (default: 0) |
| metadata | Object | No | Flexible key-value pairs for additional data |
| createdAt | Date | Yes | Record creation timestamp |
| updatedAt | Date | Yes | Last update timestamp |

**Why MongoDB for Transactions?**
1. **Flexible Schema**: The metadata field allows storing additional transaction data without schema changes
2. **High Write Performance**: Handles high-volume transaction writes efficiently
3. **Analytics-Friendly**: Easy to aggregate and analyze transaction data
4. **Graph Data**: The flexible structure makes it easy to extract data for dashboard visualizations
5. **Scalability**: MongoDB scales horizontally for growing transaction volumes

---

## Entity Relationships

```
┌─────────────┐
│    users    │
└──────┬──────┘
       │
       │ 1:N
       │
       ▼
┌──────────────────────┐
│ user_subscriptions   │
└──────┬───────────────┘
       │
       │ 1:N
       │
       ▼
┌─────────────┐        ┌──────────────────┐
│    bills    │───────▶│  subscription    │
└──────┬──────┘   N:1  │     _plans       │
       │                └──────────────────┘
       │
       │ (MongoDB Reference)
       │
       ▼
┌─────────────────────┐
│  transactions       │
│  (MongoDB)          │
└─────────────────────┘
```

---

## Data Flow

### Subscription Creation Flow
1. User selects a subscription plan
2. `UserSubscription` record created in PostgreSQL
3. Amount calculated based on billing cycle (monthly/yearly)
4. Next billing date calculated

### Bill Generation Flow
1. System creates `Bill` record for subscription
2. Bill number generated (BILL-XXXXXXXX)
3. Due date calculated based on billing cycle
4. Bill status set to PENDING

### Payment Processing Flow
1. User initiates payment for a bill
2. `Transaction` record created in MongoDB (status: PENDING)
3. Payment processed (80% success, 20% failure simulation)
4. Transaction status updated (SUCCESS or FAILED)
5. Bill status updated accordingly
6. If failed: `scheduledRetryDate` set to next day

### Payment Retry Flow
1. User initiates retry for failed transaction
2. New `Transaction` record created in MongoDB
3. `retryCount` incremented
4. Payment reprocessed with same logic
5. Statuses updated based on result

---

## Querying Examples

### PostgreSQL Queries

**Get all active subscriptions for a user:**
```sql
SELECT s.*, p.name as plan_name, p.monthly_price, p.yearly_price
FROM user_subscriptions s
JOIN subscription_plans p ON s.subscription_plan_id = p.id
WHERE s.user_id = ? AND s.status = 'ACTIVE';
```

**Get pending bills:**
```sql
SELECT b.*, u.email, u.name
FROM bills b
JOIN users u ON b.user_id = u.id
WHERE b.status = 'PENDING'
ORDER BY b.due_date ASC;
```

**Get subscription plan usage statistics:**
```sql
SELECT p.name, COUNT(s.id) as subscriber_count
FROM subscription_plans p
LEFT JOIN user_subscriptions s ON p.id = s.subscription_plan_id AND s.status = 'ACTIVE'
GROUP BY p.id, p.name
ORDER BY subscriber_count DESC;
```

### MongoDB Queries

**Get all transactions for a user:**
```javascript
db.transactions.find({ userId: 1 }).sort({ transactionDate: -1 })
```

**Get failed transactions that need retry:**
```javascript
db.transactions.find({
  status: "FAILED",
  scheduledRetryDate: { $lte: new Date() }
})
```

**Aggregate monthly spending:**
```javascript
db.transactions.aggregate([
  { $match: { status: "SUCCESS" } },
  { $group: {
      _id: {
        year: { $year: "$transactionDate" },
        month: { $month: "$transactionDate" }
      },
      totalAmount: { $sum: "$amount" },
      transactionCount: { $sum: 1 }
    }
  },
  { $sort: { "_id.year": -1, "_id.month": -1 } }
])
```

**Transaction status distribution:**
```javascript
db.transactions.aggregate([
  { $group: {
      _id: "$status",
      count: { $sum: 1 }
    }
  }
])
```

---

## Backup and Maintenance

### PostgreSQL Backup
```bash
pg_dump -U nexuspay nexuspay > nexuspay_backup.sql
```

### PostgreSQL Restore
```bash
psql -U nexuspay nexuspay < nexuspay_backup.sql
```

### MongoDB Backup
```bash
mongodump --uri="mongodb://nexuspay:nexuspay_dev@localhost:27017/nexuspay_transactions" --out=/backup/mongodb
```

### MongoDB Restore
```bash
mongorestore --uri="mongodb://nexuspay:nexuspay_dev@localhost:27017/nexuspay_transactions" /backup/mongodb
```

---

## Performance Considerations

### PostgreSQL
- Indexes on foreign keys for join performance
- Composite index on `(user_id, status)` for filtered user queries
- Regular VACUUM and ANALYZE for query optimization
- Connection pooling configured in Spring Boot

### MongoDB
- Compound indexes for common query patterns
- TTL index for automatic old transaction cleanup (if needed)
- Read preference set to nearest for analytics queries
- Write concern set to acknowledged for data consistency

---

## Migration Notes

When making schema changes:

### PostgreSQL
- Use Flyway or Liquibase for version-controlled migrations
- Always test migrations on a copy of production data
- Create rollback scripts for each migration

### MongoDB
- Schema changes are flexible due to document model
- Use migration scripts for data transformation if needed
- Maintain backward compatibility when adding new fields
