# NexusPay Setup and Launch Guide

## Overview
This guide will help you set up and run the NexusPay subscription and payment processing system. The system consists of a Spring Boot backend and a React frontend.

---

## Prerequisites

### Required Software
- **Java 17** or higher
- **Node.js 16** or higher
- **Maven 3.6** or higher
- **PostgreSQL 12** or higher
- **MongoDB 4.4** or higher

### Optional
- Docker and Docker Compose (for containerized database setup)

---

## Database Setup

### Option 1: Using Docker Compose (Recommended)

The backend includes a `compose.yaml` file that automatically sets up PostgreSQL, MongoDB, and Redis.

```bash
cd backend/apinexus
docker-compose up -d
```

This will start:
- PostgreSQL on port 5432
- MongoDB on port 27017
- Redis on port 6379

### Option 2: Manual Setup

#### PostgreSQL Setup

1. Install PostgreSQL
2. Create database and user:

```sql
CREATE DATABASE nexuspay;
CREATE USER nexuspay WITH PASSWORD 'nexuspay_dev';
GRANT ALL PRIVILEGES ON DATABASE nexuspay TO nexuspay;
```

3. The application will automatically create tables using JPA/Hibernate.

#### MongoDB Setup

1. Install MongoDB
2. Create database and user:

```javascript
use admin
db.createUser({
  user: "nexuspay",
  pwd: "nexuspay_dev",
  roles: [
    { role: "readWrite", db: "nexuspay_transactions" }
  ]
})
```

---

## Backend Setup

### 1. Navigate to Backend Directory
```bash
cd backend/apinexus
```

### 2. Configure Application Properties

The `application.properties` file is already configured for local development:

```properties
spring.application.name=apinexus
server.port=8080

# PostgreSQL Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/nexuspay
spring.datasource.username=nexuspay
spring.datasource.password=nexuspay_dev
spring.jpa.hibernate.ddl-auto=update

# MongoDB Configuration
spring.data.mongodb.uri=mongodb://nexuspay:nexuspay_dev@localhost:27017/nexuspay_transactions
```

If you're using different credentials, update this file accordingly.

### 3. Build the Backend
```bash
mvn clean compile
```

### 4. Run the Backend
```bash
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### 5. Verify Backend is Running
```bash
curl http://localhost:8080/api/users
# Should return an empty array: []
```

---

## Frontend Setup

### 1. Navigate to Frontend Directory
```bash
cd frontend/nexus
```

### 2. Install Dependencies
```bash
npm install
```

### 3. Run Development Server
```bash
npm run dev
```

The frontend will start on `http://localhost:5173` (Vite default) or `http://localhost:3000`

### 4. Build for Production (Optional)
```bash
npm run build
npm run preview
```

---

## Initial Data Setup

### Option 1: Using the Web Interface

1. Open your browser and navigate to `http://localhost:5173` (or the port shown in the terminal)
2. Click on **"Initialize Data"** in the navigation menu
3. Click the **"Initialize Data"** button
4. This will create:
   - A demo user (demo@nexuspay.com)
   - Three subscription plans (Basic, Professional, Enterprise)

### Option 2: Using API Calls

#### Create a User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "demo@nexuspay.com",
    "name": "Demo User"
  }'
```

#### Create Subscription Plans
```bash
# Basic Plan
curl -X POST http://localhost:8080/api/subscription-plans \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Basic Plan",
    "description": "Perfect for individuals and small teams",
    "monthlyPrice": 9.99,
    "yearlyPrice": 99.99,
    "features": "Up to 5 users, 10GB storage, Email support",
    "active": true
  }'

# Professional Plan
curl -X POST http://localhost:8080/api/subscription-plans \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Professional Plan",
    "description": "Ideal for growing businesses",
    "monthlyPrice": 29.99,
    "yearlyPrice": 299.99,
    "features": "Up to 50 users, 100GB storage, Priority support, API access",
    "active": true
  }'

# Enterprise Plan
curl -X POST http://localhost:8080/api/subscription-plans \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Enterprise Plan",
    "description": "For large organizations",
    "monthlyPrice": 99.99,
    "yearlyPrice": 999.99,
    "features": "Unlimited users, 1TB storage, 24/7 support, Custom integrations",
    "active": true
  }'
```

---

## Using the System

### 1. Dashboard
- Navigate to the home page to see the dashboard
- View statistics on active subscriptions, bills, and transactions
- See charts showing transaction status distribution and monthly spending

### 2. Manage Subscriptions
- Go to **Subscriptions** page
- View the subscription status indicator
- Browse available plans
- Click on a plan to select it
- Choose billing cycle (Monthly/Yearly)
- Click **Subscribe Now** to create a subscription
- View your active subscriptions and cancel if needed

### 3. Manage Payments
- Go to **Payments** page
- View pending bills
- Click **Pay Now** to initiate a payment
- The system will randomly accept (80%) or reject (20%) the payment
- Failed payments are scheduled for retry the next day
- View transaction history
- Click **Retry** on failed transactions to attempt payment again

### 4. Manage Subscription Plans (Admin)
- Go to **Plan Manager** page
- View all subscription plans
- Click **Add New Plan** to create a new plan
- Edit existing plans
- Activate/deactivate plans
- Delete plans

---

## Architecture Overview

### Backend (Spring Boot)
- **Port**: 8080
- **Database**: PostgreSQL (user data, subscriptions, bills)
- **NoSQL**: MongoDB (transaction data for flexible querying and analytics)
- **Key Features**:
  - RESTful API endpoints
  - JPA/Hibernate for PostgreSQL
  - Spring Data MongoDB for transaction storage
  - Payment simulation with 80-20 success-failure ratio
  - Automatic bill generation
  - Payment retry logic

### Frontend (React + Vite)
- **Port**: 5173 (dev) / 3000 (can be configured)
- **Key Technologies**:
  - React 19
  - React Router for navigation
  - Axios for API calls
  - Recharts for data visualization
  - Tailwind CSS for styling
- **Key Features**:
  - Dashboard with graphs and statistics
  - Subscription management interface
  - Payment processing interface
  - Plan management interface
  - Real-time subscription status checking

---

## Testing the Payment Flow

### Complete Flow Example:

1. **Create a subscription** (if not already done)
   - Go to Subscriptions page
   - Select a plan
   - Choose billing cycle
   - Subscribe

2. **Generate a bill** (automatic on subscription)
   - Bills are automatically generated when subscriptions are created

3. **View bills**
   - Go to Payments page
   - See the pending bill

4. **Make a payment**
   - Click "Pay Now" on a pending bill
   - The system will randomly succeed (80%) or fail (20%)
   - If successful: Bill marked as PAID, transaction recorded as SUCCESS
   - If failed: Bill marked as FAILED, transaction scheduled for retry next day

5. **Retry failed payment**
   - If payment failed, go to Transaction History
   - Find the failed transaction
   - Click "Retry"
   - System will attempt payment again with same 80-20 ratio

6. **View analytics**
   - Go to Dashboard
   - See transaction status distribution pie chart
   - See monthly spending bar chart
   - View bill statistics

---

## API Endpoints Summary

### Users
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create user

### Subscription Plans
- `GET /api/subscription-plans` - Get all plans
- `GET /api/subscription-plans/active` - Get active plans
- `POST /api/subscription-plans` - Create plan
- `PUT /api/subscription-plans/{id}` - Update plan
- `DELETE /api/subscription-plans/{id}` - Delete plan

### Subscriptions
- `POST /api/subscriptions` - Create subscription
- `GET /api/subscriptions/user/{userId}` - Get user subscriptions
- `GET /api/subscriptions/user/{userId}/has-active` - Check active subscription
- `DELETE /api/subscriptions/{id}` - Cancel subscription

### Bills
- `GET /api/bills/user/{userId}` - Get user bills
- `GET /api/bills/pending` - Get pending bills

### Payments
- `POST /api/payments/initiate` - Initiate payment
- `POST /api/payments/retry/{transactionId}` - Retry payment

### Transactions
- `GET /api/transactions/user/{userId}` - Get user transactions
- `GET /api/transactions/bill/{billId}` - Get bill transactions
- `GET /api/transactions/status/{status}` - Get transactions by status

---

## Troubleshooting

### Backend Issues

**Issue**: Backend fails to start
- Check if PostgreSQL and MongoDB are running
- Verify database credentials in `application.properties`
- Check if port 8080 is available

**Issue**: Database connection errors
- Verify database is running: `psql -U nexuspay -d nexuspay` or `mongosh`
- Check firewall settings
- Verify credentials

### Frontend Issues

**Issue**: API calls fail with CORS errors
- The backend has CORS enabled with `@CrossOrigin(origins = "*")`
- If issues persist, check browser console for specific errors

**Issue**: Frontend fails to build
- Delete `node_modules` and run `npm install` again
- Clear npm cache: `npm cache clean --force`

**Issue**: Charts not displaying
- Ensure you have transaction data
- Check browser console for errors

### Payment Issues

**Issue**: All payments failing
- This is normal - 20% of payments are designed to fail
- Keep trying or use the retry functionality

---

## Production Considerations

Before deploying to production:

1. **Security**
   - Change database passwords
   - Implement proper authentication and authorization
   - Use HTTPS
   - Implement rate limiting
   - Add input validation and sanitization

2. **Configuration**
   - Use environment variables for sensitive data
   - Create production profiles for Spring Boot
   - Configure proper CORS policies

3. **Database**
   - Set up database backups
   - Configure connection pooling
   - Use production-grade MongoDB clusters

4. **Monitoring**
   - Add application monitoring (e.g., Spring Boot Actuator)
   - Set up logging aggregation
   - Monitor payment success rates

5. **Testing**
   - Add comprehensive unit tests
   - Implement integration tests
   - Set up CI/CD pipeline

---

## Support

For more information, refer to:
- [API Documentation](./API_DOCUMENTATION.md)
- [Database Schema Documentation](./DATABASE_SCHEMA.md)
- Backend README: `backend/apinexus/README.md`
- Frontend README: `frontend/nexus/README.md`
