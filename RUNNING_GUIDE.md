# NexusPay - Complete Setup and Running Guide

## 🚀 Quick Start

This guide will help you get NexusPay up and running in minutes!

### Prerequisites

Ensure you have the following installed:
- ✅ **PostgreSQL 12+** (running locally on port 5432)
- ✅ **MongoDB 4.4+** (running locally on port 27017)
- ✅ **Java 17+** (for backend)
- ✅ **Maven 3.6+** (for backend build)
- ✅ **Node.js 18+** (for frontend)
- ✅ **npm** (comes with Node.js)

### Step-by-Step Setup

#### 1. Setup the Databases

**PostgreSQL:**

```bash
# Connect to PostgreSQL
psql -U postgres

# Create database and user
CREATE DATABASE nexuspay;
CREATE USER nexuspay WITH PASSWORD 'nexuspay_dev';
GRANT ALL PRIVILEGES ON DATABASE nexuspay TO nexuspay;

# Exit psql
\q
```

**MongoDB:**

```bash
# Connect to MongoDB
mongosh

# Create user with permissions
use admin
db.createUser({
  user: "nexuspay",
  pwd: "nexuspay_dev",
  roles: [
    { role: "readWrite", db: "nexuspay_transactions" },
    { role: "dbAdmin", db: "nexuspay_transactions" }
  ]
})

# Exit mongosh
exit
```

**Verify database connections:**

```bash
# Test PostgreSQL
psql -U nexuspay -d nexuspay -h localhost
# Should connect successfully, then type \q to exit

# Test MongoDB
mongosh "mongodb://nexuspay:nexuspay_dev@localhost:27017/nexuspay_transactions"
# Should connect successfully, then type exit
```

The databases will be automatically initialized with the required tables/collections when the backend starts.

#### 2. Start the Backend API Server

In a new terminal:

```bash
cd backend/apinexus
mvn spring-boot:run
```

Wait for the message: `Started ApinexusApplication in X seconds`

The backend will be available at: **http://localhost:8080**

**Test the backend:**
```bash
curl http://localhost:8080/api/health
```

You should see a JSON response indicating both databases are connected.

#### 3. Initialize Sample Data

Initialize the database with sample users, plans, and transactions:

```bash
curl -X POST http://localhost:8080/api/init/data
```

This creates:
- 3 demo users (demo@nexuspay.com, john.doe@example.com, jane.smith@example.com)
- 3 subscription plans (Basic, Professional, Enterprise)
- Sample transaction data in MongoDB

#### 4. Start the Frontend

In another terminal:

```bash
cd frontend/nexus
npm install
npm run dev
```

The frontend will be available at: **http://localhost:5173**

#### 5. Access the Application

Open your browser to **http://localhost:5173** and you should see the NexusPay dashboard!

---

## 📋 Available Services

| Service | URL | Description |
|---------|-----|-------------|
| **Frontend** | http://localhost:5173 | React web application |
| **Backend API** | http://localhost:8080/api | REST API endpoints |
| **PostgreSQL** | localhost:5432 | User, card, subscription data |
| **MongoDB** | localhost:27017 | Transaction data |
| **Health Check** | http://localhost:8080/api/health | Database connection status |

---

## 🔧 API Endpoints

### Health & Monitoring
- `GET /api/health` - Check all services health
- `GET /api/health/postgres` - Check PostgreSQL connection
- `GET /api/health/mongodb` - Check MongoDB connection

### Data Initialization
- `POST /api/init/data` - Initialize sample data
- `GET /api/init/status` - Check initialization status

### User Management
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create new user

### Card Management
- `GET /api/cards/user/{userId}` - Get user's cards
- `POST /api/cards` - Add new card
- `PUT /api/cards/{id}` - Update card
- `DELETE /api/cards/{id}` - Delete card

### Subscription Plans
- `GET /api/subscription-plans` - Get all plans
- `GET /api/subscription-plans/active` - Get active plans
- `POST /api/subscription-plans` - Create plan
- `PUT /api/subscription-plans/{id}` - Update plan
- `DELETE /api/subscription-plans/{id}` - Delete plan

### Subscriptions
- `POST /api/subscriptions` - Create subscription
- `GET /api/subscriptions/user/{userId}` - Get user subscriptions
- `DELETE /api/subscriptions/{id}` - Cancel subscription

### Bills & Payments
- `GET /api/bills/user/{userId}` - Get user bills
- `POST /api/payments/initiate` - Initiate payment
- `POST /api/payments/retry/{transactionId}` - Retry payment

### Transactions
- `GET /api/transactions/user/{userId}` - Get user transactions
- `GET /api/transactions/status/{status}` - Get transactions by status

**For complete API documentation, see:** `backend/apinexus/README.md`

---

## 🗄️ Database Architecture

### PostgreSQL Tables
- **users** - User account information (email, name)
- **card_details** - User payment card details (last 4 digits, type, expiry)
- **subscription_plans** - Available subscription plans (name, pricing, features)
- **user_subscriptions** - Active/cancelled subscriptions
- **bills** - Billing records

### MongoDB Collections
- **transactions** - Flexible transaction records with metadata for analytics

---

## 🧪 Testing the Setup

### 1. Test Health Endpoints
```bash
# Check overall health
curl http://localhost:8080/api/health

# Check PostgreSQL
curl http://localhost:8080/api/health/postgres

# Check MongoDB  
curl http://localhost:8080/api/health/mongodb
```

### 2. Test User API
```bash
# Get all users
curl http://localhost:8080/api/users

# Create a new user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","name":"Test User"}'
```

### 3. Test Subscription Plans
```bash
# Get all plans
curl http://localhost:8080/api/subscription-plans

# Get active plans only
curl http://localhost:8080/api/subscription-plans/active
```

---

## 🛑 Stopping Services

### Stop Backend
Press `Ctrl+C` in the terminal running the backend

### Stop Frontend  
Press `Ctrl+C` in the terminal running the frontend

### Stop Databases
```bash
cd backend/apinexus
docker compose down
```

To also remove data volumes:
```bash
docker compose down -v
```

---

## 🔄 Restarting After Shutdown

1. **Start databases:** `cd backend/apinexus && docker compose up -d`
2. **Start backend:** `cd backend/apinexus && mvn spring-boot:run`
3. **Start frontend:** `cd frontend/nexus && npm run dev`
4. **Initialize data (if needed):** `curl -X POST http://localhost:8080/api/init/data`

---

## 📝 Configuration Files

### Backend Configuration
- **Application Properties:** `backend/apinexus/src/main/resources/application.properties`
- **Docker Compose:** `backend/apinexus/compose.yaml`
- **Database Init Scripts:** `backend/apinexus/init-scripts/`

### Frontend Configuration
- **API Base URL:** `frontend/nexus/src/services/api.js`
- **Vite Config:** `frontend/nexus/vite.config.js`

---

## 🐛 Troubleshooting

### Backend won't start
- Ensure Java 17+ is installed: `java -version`
- Check if ports 8080 is available
- Verify databases are running: `docker ps`

### Frontend won't start
- Ensure Node.js is installed: `node -version`
- Check if port 5173 is available
- Run `npm install` in frontend directory

### Database connection errors
- Ensure Docker containers are running: `docker ps`
- Check container logs: `docker logs nexuspay-postgres` or `docker logs nexuspay-mongo`
- Restart containers: `docker compose restart`

### Data initialization fails
- Ensure backend is running
- Check backend logs for errors
- Try re-initializing: `curl -X POST http://localhost:8080/api/init/data`

---

## 📚 Additional Resources

- **API Documentation:** `backend/apinexus/README.md`
- **Database Schema:** `docs/DATABASE_SCHEMA.md`
- **Quick Start Guide:** `QUICKSTART.md`
- **Setup Guide:** `docs/SETUP_AND_LAUNCH.md`

---

## 🎉 Success!

If you've followed all the steps, you should now have:
- ✅ Databases running in Docker
- ✅ Backend API server running on port 8080
- ✅ Frontend web app running on port 5173
- ✅ Sample data loaded and ready to use

Visit **http://localhost:5173** and start exploring NexusPay!

---

## 🤝 Support

For issues or questions:
- Check the troubleshooting section above
- Review the detailed documentation in the `docs/` folder
- Open an issue on GitHub
