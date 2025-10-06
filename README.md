# NexusPay - Intelligent Multi-Tenant SaaS Billing Platform

**NexusPay** is a comprehensive, cloud-native billing automation platform designed for modern SaaS businesses. It provides intelligent billing automation, real-time usage metering, and adaptive insights to help companies scale their revenue operations efficiently.

## 🚀 What is NexusPay?

NexusPay is a powerful and intelligent "billing engine" that other online companies can plug into their business. It automates all of their complicated money-related tasks, acting as a smart digital cash register and accountant:

- **For subscription businesses**: Automatically calculates complex invoices (monthly fees + usage charges) and processes payments without manual intervention
- **For usage-based businesses**: Handles massive volumes of real-time data to bill customers accurately for exactly what they use
- **Adaptive Intelligence**: ML-powered insights help companies optimize revenue and automatically detect fraud

## 🌟 Key Features

### 💰 Comprehensive Billing Engine 
- **Multi-tier subscription management** with flexible pricing models
- **Usage-based billing** with real-time metering and aggregation
- **Automated invoice generation** with customizable templates
- **Payment processing** integration with Stripe and other gateways
- **Proration and credit handling** for plan changes
- **Tax calculation** and compliance support

### 🏢 Multi-Tenant SaaS Architecture
- **Complete tenant isolation** for security and compliance
- **Role-based access control** with granular permissions
- **Scalable microservices architecture** built on Spring Boot
- **Event-driven design** with Apache Kafka for real-time processing
- **API-first approach** with comprehensive REST APIs

### 📊 Real-Time Analytics & Intelligence
- **ML-powered revenue optimization** recommendations
- **Fraud detection** with anomaly detection algorithms
- **Real-time dashboard** with usage and revenue metrics
- **Predictive analytics** for churn prevention
- **Comprehensive reporting** and data export capabilities

## 🎯 Current Implementation

The current implementation includes a **complete subscription and payment processing system** with:

### Backend Features
- **Subscription Management**: Create, view, and cancel subscriptions with monthly/yearly billing options
- **Plan Management**: CRUD operations for subscription plans
- **Bill Generation**: Automatic bill generation based on subscription cycles
- **Payment Processing**: Simulated payment gateway with 80% success / 20% failure rate
- **Payment Retry Logic**: Automatic retry scheduling for failed payments
- **Transaction Storage**: MongoDB-based flexible transaction storage for analytics

### Frontend Features
- **Interactive Dashboard**: Visual analytics with charts and graphs
- **Subscription Manager**: User-friendly interface for managing subscriptions
- **Payment Interface**: Process payments and view transaction history
- **Plan Manager**: Admin interface for managing subscription plans
- **Real-time Status**: Check active subscription status

### Tech Stack
- **Backend**: Spring Boot 3.5, Java 17, PostgreSQL, MongoDB
- **Frontend**: React 19, Vite, Tailwind CSS, Recharts
- **APIs**: RESTful APIs with comprehensive endpoints

## 🚦 Quick Start

### Option 1: Using the Automated Startup Script (Fastest!)

```bash
git clone https://github.com/Adarsh-P-Thomson/NexusPayments.git
cd NexusPayments
./start-nexuspay.sh
```

This script automatically:
- ✅ Checks all prerequisites (Docker, Java, Maven, Node.js)
- ✅ Starts databases with Docker (PostgreSQL, MongoDB, Redis)
- ✅ Builds and starts the backend API server
- ✅ Initializes sample data (users, plans, transactions)
- ✅ Installs and starts the frontend

Then open **http://localhost:5173** and start using NexusPay!

### Option 2: Manual Setup

#### Prerequisites
- Java 17+
- Node.js 16+
- PostgreSQL 12+
- MongoDB 4.4+
- Maven 3.6+
- Docker & Docker Compose (recommended)

#### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/Adarsh-P-Thomson/NexusPayments.git
   cd NexusPayments
   ```

2. **Start databases** (using Docker Compose)
   ```bash
   cd backend/apinexus
   docker compose up -d
   ```

3. **Start backend**
   ```bash
   cd backend/apinexus
   mvn spring-boot:run
   ```
   Backend runs on `http://localhost:8080`

4. **Initialize sample data**
   ```bash
   curl -X POST http://localhost:8080/api/init/data
   ```

5. **Start frontend**
   ```bash
   cd frontend/nexus
   npm install
   npm run dev
   ```
   Frontend runs on `http://localhost:5173`

6. **Access the application**
   - Open browser to `http://localhost:5173`
   - The system is now ready with sample data loaded!

## 📚 Documentation

- **[🚀 Running Guide](./RUNNING_GUIDE.md)** - Complete setup and running instructions
- **[🚀 Quick Start Guide](./QUICKSTART.md)** - Get started in 5 minutes!
- **[🔧 Backend API Documentation](./backend/apinexus/README.md)** - Complete API reference with all endpoints
- **[Setup and Launch Guide](./docs/SETUP_AND_LAUNCH.md)** - Comprehensive setup instructions
- **[Database Schema](./docs/DATABASE_SCHEMA.md)** - Database structure and design

## 🎨 Features Walkthrough

### 1. Dashboard
View comprehensive analytics including:
- Active subscription count
- Paid and pending bills
- Total spending
- Transaction status distribution (pie chart)
- Monthly spending trends (bar chart)

### 2. Subscription Management
- Browse available subscription plans
- Check active subscription status
- Subscribe with monthly or yearly billing
- View subscription details and next billing date
- Cancel active subscriptions

### 3. Payment Processing
- View all bills (pending, paid, failed)
- Initiate payments with simulated gateway
- Automatic payment retry for failures
- View complete transaction history
- Retry failed transactions manually

### 4. Plan Management (Admin)
- Create new subscription plans
- Edit existing plans
- Activate/deactivate plans
- Set monthly and yearly pricing
- Define plan features

## 🗄️ Database Architecture

### PostgreSQL (Relational Data)
- **users** - User account information (email, name)
- **card_details** - Payment card details (last 4 digits, type, expiry, cardholder name)
- **subscription_plans** - Available subscription plans (name, pricing, features)
- **user_subscriptions** - Active/inactive user subscriptions
- **bills** - Billing records

### MongoDB (Transaction Data)
- **transactions** - Flexible transaction records with metadata
- Supports complex analytics and reporting
- Optimized for high-volume writes
- Sample data included for testing

## 🔌 API Endpoints

### Health & Monitoring
- `GET /api/health` - Check all services health (PostgreSQL + MongoDB)
- `GET /api/health/postgres` - Check PostgreSQL connection
- `GET /api/health/mongodb` - Check MongoDB connection

### Data Initialization
- `POST /api/init/data` - Initialize sample data (users, plans, transactions)
- `GET /api/init/status` - Check initialization status

### Core Endpoints
- `GET/POST /api/users` - User management
- `GET/POST/PUT/DELETE /api/cards` - Card management
- `GET/POST/PUT/DELETE /api/subscription-plans` - Plan management
- `GET/POST/DELETE /api/subscriptions` - Subscription management
- `GET /api/bills` - Bill retrieval
- `POST /api/payments/initiate` - Payment processing
- `POST /api/payments/retry/{id}` - Payment retry
- `GET /api/transactions` - Transaction history

**For complete API documentation with request/response examples, see:** [`backend/apinexus/README.md`](./backend/apinexus/README.md)

## 💳 Payment Simulation

The payment system simulates real-world payment gateway behavior:
- **80% Success Rate**: Most payments succeed
- **20% Failure Rate**: Some payments fail (simulating declined cards, insufficient funds, etc.)
- **Automatic Retry**: Failed payments are scheduled for retry the next day
- **Manual Retry**: Users can manually retry failed payments

## 🛠️ Technology Stack

### Backend
- Spring Boot 3.5.6
- Spring Data JPA (PostgreSQL)
- Spring Data MongoDB
- Lombok
- Java 17

### Frontend
- React 19
- React Router DOM
- Vite
- Tailwind CSS
- Recharts (for graphs)
- Axios (for API calls)

### Infrastructure
- PostgreSQL 12+ (RDBMS)
- MongoDB 4.4+ (NoSQL)
- Docker & Docker Compose

## 📈 Future Enhancements

- Real payment gateway integration (Stripe, PayPal)
- Email notifications for bills and payment failures
- User authentication and authorization
- Multi-tenant support
- Advanced analytics and reporting
- Scheduled billing automation
- Webhook support for external integrations
- Invoice PDF generation

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📧 Support

For questions and support, please open an issue in the GitHub repository.

---

Built with ❤️ for modern SaaS businesses

