# 🚀 NexusPay Quick Start Guide

Get your subscription and payment processing system up and running in 5 minutes!

## Prerequisites Check

Make sure you have:
- ✅ Java 17 or higher: `java -version`
- ✅ Node.js 16 or higher: `node -v`
- ✅ Maven 3.6 or higher: `mvn -v`
- ✅ Docker (optional but recommended): `docker -v`

---

## Step 1: Clone the Repository

```bash
git clone https://github.com/Adarsh-P-Thomson/NexusPayments.git
cd NexusPayments
```

---

## Step 2: Start Databases

### Option A: Using Docker (Recommended)

```bash
cd backend/apinexus
docker-compose up -d
```

This starts PostgreSQL, MongoDB, and Redis automatically.

### Option B: Manual Setup

If not using Docker, ensure you have PostgreSQL and MongoDB installed and running:

**PostgreSQL:**
```sql
CREATE DATABASE nexuspay;
CREATE USER nexuspay WITH PASSWORD 'nexuspay_dev';
GRANT ALL PRIVILEGES ON DATABASE nexuspay TO nexuspay;
```

**MongoDB:**
```javascript
use admin
db.createUser({
  user: "nexuspay",
  pwd: "nexuspay_dev",
  roles: [{ role: "readWrite", db: "nexuspay_transactions" }]
})
```

---

## Step 3: Start the Backend

Open a new terminal:

```bash
cd backend/apinexus
mvn spring-boot:run
```

Wait for the message: **"Started ApinexusApplication"**

Backend is now running on: `http://localhost:8080`

---

## Step 4: Start the Frontend

Open another new terminal:

```bash
cd frontend/nexus
npm install
npm run dev
```

Frontend is now running on: `http://localhost:5173`

---

## Step 5: Initialize Sample Data

1. Open your browser to `http://localhost:5173`
2. Click **"Initialize Data"** in the navigation menu
3. Click the **"Initialize Data"** button
4. Wait for success message

This creates:
- ✅ Demo user (demo@nexuspay.com)
- ✅ Basic Plan ($9.99/month or $99.99/year)
- ✅ Professional Plan ($29.99/month or $299.99/year)
- ✅ Enterprise Plan ($99.99/month or $999.99/year)

---

## Step 6: Explore the System!

### 🏠 Dashboard
- Navigate to home page
- View subscription statistics
- See transaction charts
- Check billing overview

### 📦 Create Your First Subscription

1. Go to **"Subscriptions"** page
2. Browse available plans
3. Click on a plan to select it
4. Choose billing cycle (Monthly or Yearly)
5. Click **"Subscribe Now"**
6. Success! You now have an active subscription

### 💳 Process Your First Payment

1. Go to **"Payments"** page
2. You'll see a pending bill for your subscription
3. Click **"Pay Now"**
4. Watch as the payment is processed (80% will succeed!)
5. If it fails, you can retry the payment

### 🔄 Retry a Failed Payment

1. In the **"Payments"** page, scroll to Transaction History
2. Find a failed transaction
3. Click **"Retry"**
4. The system will attempt payment again

### ⚙️ Manage Subscription Plans (Admin)

1. Go to **"Plan Manager"** page
2. Click **"Add New Plan"**
3. Fill in plan details
4. Click **"Create Plan"**
5. Edit or delete existing plans as needed

---

## 🎯 Key Features to Try

1. **Monthly vs Yearly Billing**
   - Subscribe with monthly billing
   - Notice the different price
   - Cancel and try yearly billing

2. **Payment Simulation**
   - Try multiple payments
   - About 80% will succeed, 20% will fail
   - This simulates real-world payment gateway behavior

3. **Payment Retry Logic**
   - When a payment fails, it's scheduled for next day
   - Manually retry to see if it succeeds
   - Each retry has same 80-20 success rate

4. **Analytics Dashboard**
   - View transaction status distribution (pie chart)
   - See monthly spending trends (bar chart)
   - Check bill statistics

---

## 🔗 Useful URLs

| Service | URL | Description |
|---------|-----|-------------|
| Frontend | http://localhost:5173 | Main web application |
| Backend API | http://localhost:8080/api | REST API endpoints |
| PostgreSQL | localhost:5432 | Relational database |
| MongoDB | localhost:27017 | Transaction database |

---

## 🧪 Testing the API

You can also test the API directly using curl:

### Get All Users
```bash
curl http://localhost:8080/api/users
```

### Get Active Subscription Plans
```bash
curl http://localhost:8080/api/subscription-plans/active
```

### Create a Subscription
```bash
curl -X POST http://localhost:8080/api/subscriptions \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "planId": 1,
    "billingCycle": "MONTHLY"
  }'
```

### Initiate Payment
```bash
curl -X POST http://localhost:8080/api/payments/initiate \
  -H "Content-Type: application/json" \
  -d '{
    "billId": 1,
    "paymentMethod": "CREDIT_CARD"
  }'
```

---

## 📚 Next Steps

- Read the [API Documentation](./docs/API_DOCUMENTATION.md)
- Explore the [Database Schema](./docs/DATABASE_SCHEMA.md)
- Check the [Complete Setup Guide](./docs/SETUP_AND_LAUNCH.md)

---

## 🐛 Troubleshooting

### Backend won't start
- ✅ Check if PostgreSQL and MongoDB are running
- ✅ Verify port 8080 is available
- ✅ Check database credentials in `application.properties`

### Frontend won't start
- ✅ Run `npm install` again
- ✅ Delete `node_modules` and reinstall
- ✅ Check if port 5173 is available

### Database connection errors
- ✅ Ensure Docker containers are running: `docker ps`
- ✅ Try restarting Docker: `docker-compose restart`
- ✅ Check database credentials match configuration

### Payment always fails
- ✅ This is normal - 20% failure rate is expected
- ✅ Keep trying or use the retry feature
- ✅ Check transaction history to see all attempts

---

## 🎉 You're All Set!

Congratulations! You now have a fully functional subscription and payment processing system running.

Happy exploring! 🚀
