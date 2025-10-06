# NexusPay API Server - Complete API Documentation

## Base URL
```
http://localhost:8080/api
```

## Overview
The NexusPay API Server provides RESTful endpoints for managing subscriptions, payments, users, and transactions. This document lists all available APIs organized by functionality.

---

## 🏥 Health & Monitoring

### Health Check (All Services)
**GET** `/health`

Check the health status of all connected services (PostgreSQL, MongoDB).

**Response:**
```json
{
  "status": "UP",
  "service": "NexusPay API Server",
  "postgres": {
    "status": "UP",
    "message": "PostgreSQL connection successful",
    "userCount": 3
  },
  "mongodb": {
    "status": "UP",
    "message": "MongoDB connection successful",
    "database": "nexuspay_transactions",
    "transactionCount": 5
  }
}
```

### PostgreSQL Health Check
**GET** `/health/postgres`

Check PostgreSQL database connection status.

### MongoDB Health Check
**GET** `/health/mongodb`

Check MongoDB database connection status.

---

## 🔧 Data Initialization

### Initialize Sample Data
**POST** `/init/data`

Initialize the database with sample users, subscription plans, and cards.

**Response:**
```json
{
  "success": true,
  "messages": [
    "Created demo user: demo@nexuspay.com",
    "Created 3 subscription plans"
  ],
  "userCount": 3,
  "planCount": 3,
  "cardCount": 3,
  "transactionCount": 3
}
```

### Get Initialization Status
**GET** `/init/status`

Check current database initialization status.

**Response:**
```json
{
  "userCount": 3,
  "planCount": 3,
  "cardCount": 3,
  "transactionCount": 3,
  "hasData": true
}
```

---

## 👥 User Management

### Get All Users
**GET** `/users`

Returns a list of all users.

**Response:**
```json
[
  {
    "id": 1,
    "email": "demo@nexuspay.com",
    "name": "Demo User",
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
]
```

### Get User by ID
**GET** `/users/{id}`

Returns a specific user by ID.

### Create User
**POST** `/users`

Create a new user.

**Request Body:**
```json
{
  "email": "user@example.com",
  "name": "John Doe"
}
```

---

## 💳 Card Management

### Get User Cards
**GET** `/cards/user/{userId}`

Get all cards for a specific user.

**Response:**
```json
[
  {
    "id": 1,
    "userId": 1,
    "cardNumberLast4": "4242",
    "cardHolderName": "Demo User",
    "cardType": "VISA",
    "expiryMonth": 12,
    "expiryYear": 2025,
    "isDefault": true,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
]
```

### Get Card by ID
**GET** `/cards/{id}`

Get a specific card by ID.

### Create Card
**POST** `/cards`

Add a new card for a user.

**Request Body:**
```json
{
  "userId": 1,
  "cardNumberLast4": "4242",
  "cardHolderName": "John Doe",
  "cardType": "VISA",
  "expiryMonth": 12,
  "expiryYear": 2025,
  "isDefault": true
}
```

### Update Card
**PUT** `/cards/{id}`

Update an existing card.

### Delete Card
**DELETE** `/cards/{id}`

Delete a card.

---

## 📋 Subscription Plans

### Get All Plans
**GET** `/subscription-plans`

Returns all subscription plans.

### Get Active Plans
**GET** `/subscription-plans/active`

Returns only active subscription plans.

### Get Plan by ID
**GET** `/subscription-plans/{id}`

Returns a specific plan by ID.

### Create Plan
**POST** `/subscription-plans`

Create a new subscription plan.

**Request Body:**
```json
{
  "name": "Professional Plan",
  "description": "Ideal for growing businesses",
  "monthlyPrice": 29.99,
  "yearlyPrice": 299.99,
  "features": "Up to 50 users, 100GB storage, Priority support",
  "active": true
}
```

### Update Plan
**PUT** `/subscription-plans/{id}`

Update an existing plan.

### Delete Plan
**DELETE** `/subscription-plans/{id}`

Delete a subscription plan.

---

## 📊 Subscriptions

### Create Subscription
**POST** `/subscriptions`

Create a new user subscription.

**Request Body:**
```json
{
  "userId": 1,
  "subscriptionPlanId": 2,
  "billingCycle": "MONTHLY"
}
```

### Get User Subscriptions
**GET** `/subscriptions/user/{userId}`

Get all subscriptions for a user.

### Check Active Subscription
**GET** `/subscriptions/user/{userId}/has-active`

Check if user has an active subscription.

**Response:**
```json
{
  "hasActiveSubscription": true
}
```

### Cancel Subscription
**DELETE** `/subscriptions/{subscriptionId}`

Cancel a subscription.

---

## 💰 Bills

### Get User Bills
**GET** `/bills/user/{userId}`

Get all bills for a specific user.

**Response:**
```json
[
  {
    "id": 1,
    "userId": 1,
    "subscriptionId": 1,
    "billNumber": "BILL-ABC12345",
    "amount": 29.99,
    "status": "PENDING",
    "billingPeriodStart": "2024-01-01T00:00:00",
    "billingPeriodEnd": "2024-02-01T00:00:00",
    "dueDate": "2024-02-05T00:00:00",
    "createdAt": "2024-01-01T00:00:00"
  }
]
```

### Get Pending Bills
**GET** `/bills/pending`

Get all pending bills across all users.

---

## 💸 Payments

### Initiate Payment
**POST** `/payments/initiate`

Initiate a payment for a bill.

**Request Body:**
```json
{
  "billId": 1,
  "paymentMethod": "CREDIT_CARD"
}
```

**Response:**
```json
{
  "success": true,
  "transactionId": "TXN-1234567890",
  "status": "SUCCESS",
  "message": "Payment processed successfully",
  "amount": 29.99
}
```

### Retry Payment
**POST** `/payments/retry/{transactionId}`

Retry a failed payment.

---

## 📈 Transactions

### Get User Transactions
**GET** `/transactions/user/{userId}`

Get all transactions for a specific user.

**Response:**
```json
[
  {
    "id": "507f1f77bcf86cd799439011",
    "userId": 1,
    "billId": 1,
    "transactionId": "TXN-1234567890",
    "amount": 29.99,
    "status": "SUCCESS",
    "paymentMethod": "CREDIT_CARD",
    "transactionDate": "2024-01-01T10:00:00",
    "failureReason": null,
    "retryCount": 0,
    "metadata": {
      "cardLast4": "4242",
      "cardType": "VISA"
    },
    "createdAt": "2024-01-01T10:00:00"
  }
]
```

### Get Bill Transactions
**GET** `/transactions/bill/{billId}`

Get all transactions for a specific bill.

### Get Transactions by Status
**GET** `/transactions/status/{status}`

Get all transactions with a specific status (SUCCESS, FAILED, PENDING, etc.).

---

## 🔄 Transaction Status Values

- `PENDING` - Payment is being processed
- `SUCCESS` - Payment completed successfully
- `FAILED` - Payment failed
- `RETRYING` - Payment is being retried
- `CANCELLED` - Payment was cancelled

## 📝 Billing Cycle Values

- `MONTHLY` - Monthly billing cycle
- `YEARLY` - Yearly billing cycle

## 🏷️ Bill Status Values

- `PENDING` - Bill is awaiting payment
- `PAID` - Bill has been paid
- `FAILED` - Payment failed
- `CANCELLED` - Bill was cancelled

## 🔐 CORS Configuration

All endpoints support Cross-Origin Resource Sharing (CORS) from any origin (`*`). In production, this should be restricted to specific origins.

---

## 🚀 Quick Start

1. **Start the databases:**
   ```bash
   cd backend/apinexus
   docker-compose up -d
   ```

2. **Start the API server:**
   ```bash
   mvn spring-boot:run
   ```

3. **Initialize sample data:**
   ```bash
   curl -X POST http://localhost:8080/api/init/data
   ```

4. **Check health:**
   ```bash
   curl http://localhost:8080/api/health
   ```

---

## 📊 Database Architecture

### PostgreSQL (Relational Data)
- **users** - User account information
- **card_details** - User credit card details
- **subscription_plans** - Available subscription plans
- **user_subscriptions** - Active/inactive user subscriptions
- **bills** - Billing records

### MongoDB (Transaction Data)
- **transactions** - Flexible transaction records with metadata

---

## 🛠️ Technology Stack

- **Framework:** Spring Boot 3.5.6
- **Language:** Java 17
- **PostgreSQL:** JPA/Hibernate
- **MongoDB:** Spring Data MongoDB
- **Build Tool:** Maven

---

## 📞 Support

For issues or questions, please refer to the main documentation or open an issue in the repository.
