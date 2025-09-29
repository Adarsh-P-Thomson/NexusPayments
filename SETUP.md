# NexusPay Complete Setup Guide

This comprehensive guide walks you through setting up the complete NexusPay development environment from scratch. Follow these steps for a fully functional local development setup.

## 📋 Prerequisites

### **System Requirements**

#### **Minimum Hardware Requirements**
- **CPU**: 4 cores (8 recommended)
- **RAM**: 8GB (16GB recommended for smooth development)
- **Storage**: 10GB free space (SSD recommended)
- **Network**: Stable internet connection for downloading dependencies

#### **Operating System Support**
- **macOS**: 10.15+ (Catalina or newer)
- **Linux**: Ubuntu 20.04+, CentOS 8+, or equivalent distributions
- **Windows**: Windows 10/11 with WSL2 (recommended) or native support

### **Required Software Versions**

| Component | Minimum Version | Recommended | Notes |
|-----------|-----------------|-------------|-------|
| **Docker** | 20.10+ | 24.0+ | Container platform |
| **Docker Compose** | 2.0+ | 2.20+ | Multi-container orchestration |
| **Java** | OpenJDK 17 | OpenJDK 21 | Backend runtime |
| **Maven** | 3.6+ | 3.9+ | Java build tool |
| **Node.js** | 18.0+ | 20.0+ | Frontend runtime |
| **npm** | 8.0+ | 10.0+ | JavaScript package manager |
| **Git** | 2.30+ | 2.40+ | Version control |

## 🚀 Installation Instructions

### **macOS Installation**

#### **Option 1: Using Homebrew (Recommended)**
```bash
# Install Homebrew if not already installed
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install all required tools
brew install openjdk@17 maven node docker git
brew install --cask docker

# Set JAVA_HOME environment variable
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# Start Docker Desktop
open /Applications/Docker.app

# Verify installations
java --version    # Should show OpenJDK 17+
mvn --version     # Should show Maven 3.6+
node --version    # Should show Node 18+
docker --version  # Should show Docker 20.10+
```

#### **Option 2: Manual Installation**
```bash
# Download and install Java 17
curl -O https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_macos-x64_bin.tar.gz
sudo tar -xzf openjdk-17.0.2_macos-x64_bin.tar.gz -C /Library/Java/JavaVirtualMachines/

# Download and install Maven
curl -O https://archive.apache.org/dist/maven/maven-3/3.9.4/binaries/apache-maven-3.9.4-bin.tar.gz
sudo tar -xzf apache-maven-3.9.4-bin.tar.gz -C /opt/
sudo ln -s /opt/apache-maven-3.9.4 /opt/maven

# Install Node.js from official installer
curl -O https://nodejs.org/dist/v20.8.0/node-v20.8.0.pkg
# Open and run the installer

# Download Docker Desktop from https://www.docker.com/products/docker-desktop/
```

### **Ubuntu/Debian Installation**

```bash
# Update package index
sudo apt update

# Install Java 17
sudo apt install openjdk-17-jdk openjdk-17-jre

# Install Maven
sudo apt install maven

# Install Node.js from NodeSource repository
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt-get install -y nodejs

# Install Docker and Docker Compose
sudo apt-get install ca-certificates curl gnupg
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg

echo \
  "deb [arch="$(dpkg --print-architecture)" signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  "$(. /etc/os-release && echo "$VERSION_CODENAME")" stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

sudo apt-get update
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Add user to docker group (requires logout/login)
sudo usermod -aG docker $USER
newgrp docker

# Install Git
sudo apt install git

# Verify installations
java --version && mvn --version && node --version && docker --version
```

### **CentOS/RHEL Installation**

```bash
# Install Java 17
sudo dnf install java-17-openjdk java-17-openjdk-devel

# Install Maven
sudo dnf install maven

# Install Node.js
sudo dnf module install nodejs:18/common

# Install Docker
sudo dnf install dnf-plugins-core
sudo dnf config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
sudo dnf install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Enable and start Docker
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker $USER

# Install Git
sudo dnf install git

# Set JAVA_HOME
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk' >> ~/.bashrc
source ~/.bashrc
```

### **Windows Installation**

#### **Option 1: Using WSL2 (Recommended)**
```powershell
# Enable WSL2
wsl --install

# Install Ubuntu in WSL2
wsl --install -d Ubuntu

# Follow Ubuntu installation steps inside WSL2
# Install Docker Desktop for Windows with WSL2 backend
```

#### **Option 2: Native Windows with Chocolatey**
```powershell
# Install Chocolatey package manager
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Install required software
choco install openjdk17 maven nodejs docker-desktop git

# Set environment variables
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\OpenJDK\jdk-17", [System.EnvironmentVariableTarget]::Machine)
[System.Environment]::SetEnvironmentVariable("PATH", $env:PATH + ";%JAVA_HOME%\bin", [System.EnvironmentVariableTarget]::Machine)

# Verify installations (restart terminal first)
java --version; mvn --version; node --version; docker --version
```

## 🔧 Environment Configuration

### **1. Git Configuration**
```bash
# Configure Git (replace with your information)
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"

# Configure Git to handle line endings (important for cross-platform development)
git config --global core.autocrlf input  # macOS/Linux
git config --global core.autocrlf true   # Windows
```

### **2. Docker Configuration**

#### **Docker Resources (Adjust based on your system)**
```yaml
# Docker Desktop Settings (via UI or ~/.docker/daemon.json)
{
  "experimental": false,
  "debug": true,
  "hosts": ["unix:///var/run/docker.sock"],
  "log-opts": {
    "max-size": "10m",
    "max-file": "3"
  },
  "default-address-pools": [
    {
      "base": "172.80.0.0/12",
      "size": 24
    }
  ]
}
```

#### **Docker Resource Allocation**
- **Memory**: Minimum 4GB, recommended 8GB
- **CPU**: At least 2 cores allocated to Docker
- **Disk**: At least 5GB for images and containers

### **3. Java Environment Variables**
```bash
# Add to ~/.bashrc, ~/.zshrc, or ~/.profile
export JAVA_HOME=$(readlink -f /usr/bin/java | sed "s:bin/java::")  # Linux
export JAVA_HOME=$(/usr/libexec/java_home -v 17)                   # macOS
export PATH="$JAVA_HOME/bin:$PATH"
export MAVEN_OPTS="-Xmx2g -XX:MaxPermSize=512m"

# For Windows (set via System Properties > Environment Variables)
JAVA_HOME=C:\Program Files\OpenJDK\jdk-17
PATH=%JAVA_HOME%\bin;%PATH%
```

### **4. Node.js Configuration**
```bash
# Set npm registry and configure for optimal performance
npm config set registry https://registry.npmjs.org/
npm config set cache ~/.npm-cache
npm config set fund false
npm config set audit-level moderate

# Install useful global packages
npm install -g npm@latest
npm install -g typescript
npm install -g @vite/create-app
```

## 🏗️ NexusPay Project Setup

### **1. Repository Clone and Initial Setup**

```bash
# Clone the repository
git clone https://github.com/Adarsh-P-Thomson/NexusPayments.git
cd NexusPayments

# Create required directories for data persistence
mkdir -p logs
mkdir -p data/{postgres,mongo,kafka,redis,minio}

# Make all scripts executable (macOS/Linux)
chmod +x scripts/*.sh

# For Windows (if not using WSL2)
# Ensure scripts are executable or use Git Bash
```

### **2. Environment Variables Setup**

Create environment configuration files for different environments:

#### **Development Environment (.env.dev)**
```bash
# Create development environment file
cat > .env.dev << 'EOF'
# === NexusPay Development Configuration ===

# Application Environment
ENVIRONMENT=development
LOG_LEVEL=DEBUG

# Database Configuration
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=nexuspay
POSTGRES_USER=nexuspay
POSTGRES_PASSWORD=nexuspay_dev

# MongoDB Configuration
MONGO_HOST=localhost
MONGO_PORT=27017
MONGO_DB=events
MONGO_USER=nexuspay
MONGO_PASSWORD=nexuspay_dev

# Kafka Configuration
KAFKA_BROKERS=localhost:9092
KAFKA_SCHEMA_REGISTRY=http://localhost:8081

# Redis Configuration
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=nexuspay_dev

# JWT Configuration
JWT_SECRET=nexuspay-dev-secret-key-change-in-production
JWT_EXPIRATION=3600

# External Services (Development)
STRIPE_SECRET_KEY=sk_test_your_stripe_test_key
STRIPE_WEBHOOK_SECRET=whsec_your_webhook_secret
SENDGRID_API_KEY=your_sendgrid_api_key

# Frontend Configuration
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_WS_URL=ws://localhost:8080/ws
VITE_ENVIRONMENT=development
EOF
```

#### **Production Environment (.env.prod)**
```bash
# Create production environment template (DO NOT commit to Git)
cat > .env.prod.template << 'EOF'
# === NexusPay Production Configuration ===

# Application Environment
ENVIRONMENT=production
LOG_LEVEL=INFO

# Database Configuration (Use managed services in production)
POSTGRES_HOST=your-production-postgres-host
POSTGRES_PORT=5432
POSTGRES_DB=nexuspay_prod
POSTGRES_USER=nexuspay_prod
POSTGRES_PASSWORD=your-secure-password

# MongoDB Configuration
MONGO_HOST=your-production-mongo-host
MONGO_PORT=27017
MONGO_DB=events_prod
MONGO_USER=nexuspay_prod
MONGO_PASSWORD=your-secure-password

# Security Configuration
JWT_SECRET=your-secure-jwt-secret-minimum-256-bits
JWT_EXPIRATION=3600
BCRYPT_ROUNDS=12

# External Services (Production)
STRIPE_SECRET_KEY=sk_live_your_stripe_live_key
STRIPE_WEBHOOK_SECRET=whsec_your_production_webhook_secret
SENDGRID_API_KEY=your_production_sendgrid_key

# Monitoring & Observability
PROMETHEUS_ENDPOINT=http://your-prometheus:9090
JAEGER_ENDPOINT=http://your-jaeger:14268/api/traces
GRAFANA_ENDPOINT=http://your-grafana:3000
EOF

# Add to .gitignore to prevent committing secrets
echo ".env.prod" >> .gitignore
```

### **3. Infrastructure Services Startup**

#### **Quick Start (Automated)**
```bash
# Start all infrastructure services with a single command
./scripts/dev-start.sh

# This script will:
# 1. Check Docker is running
# 2. Create necessary directories
# 3. Start Docker Compose services
# 4. Wait for services to be ready
# 5. Create default Kafka topics
# 6. Display service URLs and connection info
```

#### **Manual Step-by-Step Startup**
```bash
# Start core infrastructure services
docker compose -f infra/docker-compose.yml up -d postgres mongo kafka redis

# Wait for services to start (about 30-60 seconds)
docker compose -f infra/docker-compose.yml logs -f postgres

# Start monitoring and management services
docker compose -f infra/docker-compose.yml up -d prometheus grafana jaeger kafka-ui minio

# Verify all services are running
docker ps
# Should show 8-9 running containers
```

#### **Service Health Verification**
```bash
# Check PostgreSQL connection
docker exec -it nexus-postgres pg_isready -U nexuspay
# Expected: localhost:5432 - accepting connections

# Check MongoDB connection
docker exec -it nexus-mongo mongosh --eval "db.adminCommand('ping')"
# Expected: { ok: 1 }

# Check Kafka broker
docker exec -it nexus-kafka kafka-topics --bootstrap-server localhost:9092 --list
# Expected: List of topics including __consumer_offsets

# Check Redis connection
docker exec -it nexus-redis redis-cli ping
# Expected: PONG

# Access web interfaces
curl -s http://localhost:8090 | grep -q "Kafka UI" && echo "Kafka UI: ✅" || echo "Kafka UI: ❌"
curl -s http://localhost:3001 | grep -q "Grafana" && echo "Grafana: ✅" || echo "Grafana: ❌"
curl -s http://localhost:9090 | grep -q "Prometheus" && echo "Prometheus: ✅" || echo "Prometheus: ❌"
curl -s http://localhost:16686 | grep -q "Jaeger" && echo "Jaeger: ✅" || echo "Jaeger: ❌"
```

### **4. Backend Services Setup**

#### **Build and Start Backend Services**
```bash
# Option 1: Use automated script
./scripts/start-backend.sh

# Option 2: Manual build and start
cd backend

# Clean and compile all services
mvn clean compile -T 4  # Use 4 threads for faster compilation

# Run tests to ensure everything works
mvn test

# Package services
mvn package -DskipTests  # Skip tests for faster packaging

# Start Identity Service (currently the only implemented service)
cd nexus-identity-service
mvn spring-boot:run -Dspring.profiles.active=dev

# In separate terminals, start other services as they are implemented:
# cd nexus-api-gateway && mvn spring-boot:run
# cd nexus-billing-service && mvn spring-boot:run
# cd nexus-metering-service && mvn spring-boot:run
# cd nexus-ml-service && mvn spring-boot:run
```

#### **Backend Service Ports**
| Service | Port | Status | Health Check |
|---------|------|---------|--------------|
| **API Gateway** | 8080 | Planned | `curl http://localhost:8080/actuator/health` |
| **Identity Service** | 8081 | ✅ Active | `curl http://localhost:8081/api/v1/health` |
| **Billing Service** | 8082 | Planned | `curl http://localhost:8082/actuator/health` |
| **Metering Service** | 8083 | Planned | `curl http://localhost:8083/actuator/health` |
| **ML Service** | 8084 | Planned | `curl http://localhost:8084/actuator/health` |

### **5. Frontend Application Setup**

#### **Quick Start**
```bash
# Use automated script
./scripts/start-frontend.sh

# Manual setup
cd frontend

# Install dependencies (first time or when package.json changes)
npm install

# Start development server with hot reload
npm run dev

# The application will be available at http://localhost:3000
```

#### **Frontend Development Commands**
```bash
# Install dependencies
npm install

# Start development server
npm run dev              # Starts on http://localhost:3000

# Build for production
npm run build            # Outputs to dist/

# Preview production build
npm run preview          # Serves dist/ on http://localhost:4173

# Linting and code quality
npm run lint             # ESLint checking
npm run lint:fix         # Auto-fix linting issues

# Type checking
npx tsc --noEmit         # TypeScript type checking without build
```

### **6. Database Initialization and Seeding**

#### **PostgreSQL Setup**
```bash
# Connect to PostgreSQL
docker exec -it nexus-postgres psql -U nexuspay -d nexuspay

# Create schemas for different services
CREATE SCHEMA IF NOT EXISTS identity;
CREATE SCHEMA IF NOT EXISTS billing;
CREATE SCHEMA IF NOT EXISTS notifications;

# Create basic tables (more will be added with Flyway migrations)
\c nexuspay

-- Identity Service Tables
CREATE TABLE IF NOT EXISTS identity.tenants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    subdomain VARCHAR(100) UNIQUE NOT NULL,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS identity.users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID REFERENCES identity.tenants(id),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    email_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert test data
INSERT INTO identity.tenants (name, subdomain) VALUES 
    ('NexusPay Demo', 'demo'),
    ('Test Company', 'testco')
ON CONFLICT (subdomain) DO NOTHING;

\q
```

#### **MongoDB Setup**
```bash
# Connect to MongoDB
docker exec -it nexus-mongo mongosh -u nexuspay -p nexuspay_dev

# Switch to events database
use events

# Create collections with proper indexes
db.createCollection("usage_events")
db.createCollection("billing_events")
db.createCollection("audit_logs")

# Create indexes for performance
db.usage_events.createIndex({ "tenant_id": 1, "timestamp": -1 })
db.usage_events.createIndex({ "user_id": 1, "event_type": 1 })
db.billing_events.createIndex({ "tenant_id": 1, "created_at": -1 })
db.audit_logs.createIndex({ "tenant_id": 1, "timestamp": -1 })

# Insert sample data
db.usage_events.insertMany([
    {
        tenant_id: "demo",
        user_id: "user-1",
        event_type: "api_call",
        quantity: 1,
        timestamp: new Date(),
        metadata: { endpoint: "/api/v1/users", method: "GET" }
    },
    {
        tenant_id: "demo", 
        user_id: "user-1",
        event_type: "storage_used",
        quantity: 1024,
        timestamp: new Date(),
        metadata: { file_type: "image", size_bytes: 1024 }
    }
])

exit
```

#### **Kafka Topics Setup**
```bash
# The dev-start.sh script automatically creates these topics, but you can create them manually:

# Connect to Kafka container
docker exec -it nexus-kafka bash

# Create necessary topics
kafka-topics --create --topic billing-events --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics --create --topic usage-events --bootstrap-server localhost:9092 --partitions 6 --replication-factor 1
kafka-topics --create --topic notification-events --bootstrap-server localhost:9092 --partitions 2 --replication-factor 1
kafka-topics --create --topic audit-events --bootstrap-server localhost:9092 --partitions 2 --replication-factor 1

# List all topics to verify
kafka-topics --list --bootstrap-server localhost:9092

exit
```
```

The frontend will be available at: http://localhost:3000

## Service URLs

### Application Services
- **Frontend**: http://localhost:3000
- **Identity Service**: http://localhost:8081
- **API Gateway**: http://localhost:8080 (when implemented)

### Infrastructure Services
- **Kafka UI**: http://localhost:8090
- **MinIO Console**: http://localhost:9001 (admin: nexuspay/nexuspay_dev123)
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3001 (admin: admin/nexuspay_dev)
- **Jaeger**: http://localhost:16686

### Database Connections
- **PostgreSQL**: localhost:5432
  - Database: nexuspay
  - Username: nexuspay
  - Password: nexuspay_dev
- **MongoDB**: localhost:27017
  - Username: nexuspay
  - Password: nexuspay_dev
- **Redis**: localhost:6379
  - Password: nexuspay_dev

## Development Workflow

### Backend Development

1. **Build all services**:
   ```bash
   cd backend
   mvn clean compile
   ```

2. **Run tests**:
   ```bash
   cd backend
   mvn test
   ```

3. **Package services**:
   ```bash
   cd backend
   mvn package
   ```

### Frontend Development

1. **Install dependencies**:
   ```bash
   cd frontend
   npm install
   ```

2. **Run development server**:
   ```bash
   cd frontend
   npm run dev
   ```

3. **Build for production**:
   ```bash
   cd frontend
   npm run build
   ```

4. **Run linting**:
   ```bash
   cd frontend
   npm run lint
   ```

## Project Structure

```
NexusPayments/
├── backend/                     # Java Spring Boot microservices
│   ├── nexus-identity-service/  # User authentication & authorization
│   ├── nexus-api-gateway/      # API Gateway (to be implemented)
│   ├── nexus-billing-service/  # Billing engine (to be implemented)
│   ├── nexus-metering-service/ # Usage tracking (to be implemented)
│   ├── nexus-ml-service/       # ML analytics (to be implemented)
│   └── pom.xml                 # Parent Maven configuration
├── frontend/                   # React TypeScript web application
├── infra/                      # Infrastructure configuration
│   ├── docker-compose.yml      # Development infrastructure
│   ├── scripts/                # Database initialization scripts
│   └── monitoring/             # Monitoring configuration
├── scripts/                    # Development utility scripts
├── libs/                       # Shared libraries
└── docs/                       # Project documentation
```

## Stopping Services

### Stop Backend Services
```bash
./scripts/stop-backend.sh
```

### Stop All Services (including infrastructure)
```bash
./scripts/stop-all.sh
```

## Troubleshooting

### Common Issues

1. **Port conflicts**: Make sure ports 3000, 5432, 6379, 8080-8090, 9000-9001, 9090, 16686, 27017 are available
2. **Docker not running**: Ensure Docker Desktop is running
3. **Java version**: Make sure Java 17+ is installed and JAVA_HOME is set
4. **Node.js version**: Ensure Node.js 18+ is installed

### Logs

- Backend service logs are available in the `logs/` directory
- Infrastructure logs: `docker compose -f infra/docker-compose.yml logs -f [service-name]`

### Health Checks

- Identity Service: http://localhost:8081/api/v1/health
- Frontend connection test: Use the "Check Backend Connection" button on the homepage

## Next Steps

1. Implement additional microservices (API Gateway, Billing Service, etc.)
2. Add database migrations with Flyway
3. Implement authentication and authorization
4. Add comprehensive testing
5. Set up CI/CD pipelines
6. Configure production deployments

For detailed implementation guidelines, see the documentation in the `docs/` directory.