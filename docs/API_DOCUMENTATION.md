# NexusPay API Documentation

## Overview
This document describes the REST API endpoints for the NexusPay subscription and payment processing system.

Base URL: `http://localhost:8080/api`

---

## User Management

### Get All Users
```
GET /users
```
Returns a list of all users.

### Get User by ID
```
GET /users/{id}
```
Returns a specific user by ID.

### Create User
```
POST /users
Content-Type: application/json

{
  "email": "user@example.com",
  "name": "John Doe"
}
```

---

## Subscription Plans

### Get All Plans
```
GET /subscription-plans
```
Returns all subscription plans.

### Get Active Plans
```
GET /subscription-plans/active
```
Returns only active subscription plans.

### Get Plan by ID
```
GET /subscription-plans/{id}
```
Returns a specific plan by ID.

### Create Plan
```
POST /subscription-plans
Content-Type: application/json

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
```
PUT /subscription-plans/{id}
Content-Type: application/json

{
  "name": "Professional Plan Updated",
  "description": "Updated description",
  "monthlyPrice": 34.99,
  "yearlyPrice": 349.99,
  "features": "Updated features",
  "active": true
}
```

### Delete Plan
```
DELETE /subscription-plans/{id}
```

---

## Subscriptions

### Create Subscription
```
POST /subscriptions
Content-Type: application/json

{
  "userId": 1,
  "planId": 1,
  "billingCycle": "MONTHLY"  // or "YEARLY"
}
```

**Response:**
```json
{
  "id": 1,
  "user": { ... },
  "subscriptionPlan": { ... },
  "billingCycle": "MONTHLY",
  "status": "ACTIVE",
  "startDate": "2024-01-01T00:00:00",
  "nextBillingDate": "2024-02-01T00:00:00",
  "amount": 29.99,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

### Get User Subscriptions
```
GET /subscriptions/user/{userId}
```
Returns all subscriptions for a specific user.

### Check Active Subscription
```
GET /subscriptions/user/{userId}/has-active
```
Returns whether the user has an active subscription.

**Response:**
```json
{
  "hasActiveSubscription": true
}
```

### Cancel Subscription
```
DELETE /subscriptions/{subscriptionId}
```

---

## Bills

### Get User Bills
```
GET /bills/user/{userId}
```
Returns all bills for a specific user.

**Response:**
```json
[
  {
    "id": 1,
    "user": { ... },
    "subscription": { ... },
    "billNumber": "BILL-ABC12345",
    "amount": 29.99,
    "status": "PENDING",
    "billingPeriodStart": "2024-01-01T00:00:00",
    "billingPeriodEnd": "2024-02-01T00:00:00",
    "dueDate": "2024-01-08T00:00:00",
    "paidDate": null,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  }
]
```

### Get Pending Bills
```
GET /bills/pending
```
Returns all pending bills across all users.

---

## Payments

### Initiate Payment
```
POST /payments/initiate
Content-Type: application/json

{
  "billId": 1,
  "paymentMethod": "CREDIT_CARD"
}
```

**Response:**
```json
{
  "success": true,
  "transactionId": "TXN-XYZ123456789",
  "message": "Payment processed successfully"
}
```

**Note:** The payment has an 80% success rate and 20% failure rate (randomized). If it fails, the payment is scheduled for retry the next day.

### Retry Payment
```
POST /payments/retry/{transactionId}
```
Retry a failed payment transaction.

**Response:**
```json
{
  "success": true,
  "transactionId": "TXN-ABC987654321",
  "message": "Payment retry successful"
}
```

---

## Transactions

### Get User Transactions
```
GET /transactions/user/{userId}
```
Returns all transactions for a specific user.

**Response:**
```json
[
  {
    "id": "65f123abc456def789",
    "userId": 1,
    "billId": 1,
    "transactionId": "TXN-XYZ123456789",
    "amount": 29.99,
    "status": "SUCCESS",
    "paymentMethod": "CREDIT_CARD",
    "transactionDate": "2024-01-01T10:30:00",
    "scheduledRetryDate": null,
    "failureReason": null,
    "retryCount": 0,
    "metadata": {},
    "createdAt": "2024-01-01T10:30:00",
    "updatedAt": "2024-01-01T10:30:00"
  }
]
```

### Get Bill Transactions
```
GET /transactions/bill/{billId}
```
Returns all transactions for a specific bill.

### Get Transactions by Status
```
GET /transactions/status/{status}
```
Returns all transactions with a specific status (PENDING, SUCCESS, FAILED, RETRYING, CANCELLED).

---

## Payment Processing Logic

The payment processing service implements a simple simulation:
- **80% Success Rate**: Payments have an 80% chance of succeeding
- **20% Failure Rate**: Payments have a 20% chance of failing
- **Automatic Retry**: Failed payments are scheduled for retry the next day
- **Retry Logic**: Retry attempts also follow the 80-20 success-failure ratio

This simulates real-world payment gateway behavior where some payments may fail due to various reasons (insufficient funds, card issues, etc.).
