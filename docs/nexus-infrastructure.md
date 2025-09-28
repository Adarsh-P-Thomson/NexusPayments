# NexusPay Infrastructure & Development Setup

Complete infrastructure setup using Docker Compose for local development and Terraform for cloud deployment.

## Local Development Setup

### Docker Compose (infra/docker-compose.yml)

```yaml
version: '3.8'

services:
  # PostgreSQL Database for transactional data
  postgres:
    image: postgres:15-alpine
    container_name: nexus-postgres
    environment:
      POSTGRES_DB: nexuspay
      POSTGRES_USER: nexuspay
      POSTGRES_PASSWORD: nexuspay_dev
      POSTGRES_MULTIPLE_DATABASES: billing,identity,metering
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./scripts/init-databases.sh:/docker-entrypoint-initdb.d/init-databases.sh:ro
    networks:
      - nexuspay-network

  # MongoDB for event storage
  mongo:
    image: mongo:7.0
    container_name: nexus-mongo
    environment:
      MONGO_INITDB_ROOT_USERNAME: nexuspay
      MONGO_INITDB_ROOT_PASSWORD: nexuspay_dev
      MONGO_INITDB_DATABASE: events
    ports:
      - "27017:27017"
    volumes:
      - mongo_data:/data/db
      - ./scripts/init-mongo.js:/docker-entrypoint-initdb.d/init-mongo.js:ro
    networks:
      - nexuspay-network

  # Apache Kafka for event streaming
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    container_name: nexus-zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    volumes:
      - zk_data:/var/lib/zookeeper/data
      - zk_logs:/var/lib/zookeeper/log
    networks:
      - nexuspay-network

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    container_name: nexus-kafka
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
      - "9094:9094"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_METRIC_REPORTERS: io.confluent.metrics.reporter.ConfluentMetricsReporter
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
      KAFKA_CONFLUENT_METRICS_REPORTER_BOOTSTRAP_SERVERS: kafka:29092
      KAFKA_CONFLUENT_METRICS_REPORTER_TOPIC_REPLICAS: 1
      KAFKA_CONFLUENT_METRICS_ENABLE: 'true'
      KAFKA_CONFLUENT_SUPPORT_CUSTOMER_ID: anonymous
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: 'true'
    volumes:
      - kafka_data:/var/lib/kafka/data
    networks:
      - nexuspay-network
    healthcheck:
      test: kafka-topics --bootstrap-server kafka:9092 --list
      interval: 30s
      timeout: 10s
      retries: 3

  # Redis for caching and session storage
  redis:
    image: redis:7.2-alpine
    container_name: nexus-redis
    ports:
      - "6379:6379"
    command: redis-server --appendonly yes --requirepass nexuspay_dev
    volumes:
      - redis_data:/data
    networks:
      - nexuspay-network

  # Kafka UI for development
  kafka-ui:
    image: provectuslabs/kafka-ui:latest
    container_name: nexus-kafka-ui
    depends_on:
      - kafka
    ports:
      - "8090:8080"
    environment:
      KAFKA_CLUSTERS_0_NAME: local
      KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka:29092
      KAFKA_CLUSTERS_0_ZOOKEEPER: zookeeper:2181
    networks:
      - nexuspay-network

  # MinIO for object storage (S3 compatible)
  minio:
    image: minio/minio:latest
    container_name: nexus-minio
    ports:
      - "9000:9000"
      - "9001:9001"
    environment:
      MINIO_ROOT_USER: nexuspay
      MINIO_ROOT_PASSWORD: nexuspay_dev123
    command: server /data --console-address ":9001"
    volumes:
      - minio_data:/data
    networks:
      - nexuspay-network

  # Prometheus for metrics collection
  prometheus:
    image: prom/prometheus:latest
    container_name: nexus-prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml:ro
      - prometheus_data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/etc/prometheus/console_libraries'
      - '--web.console.templates=/etc/prometheus/consoles'
      - '--storage.tsdb.retention.time=200h'
      - '--web.enable-lifecycle'
    networks:
      - nexuspay-network

  # Grafana for monitoring dashboards
  grafana:
    image: grafana/grafana:latest
    container_name: nexus-grafana
    ports:
      - "3001:3000"
    environment:
      GF_SECURITY_ADMIN_PASSWORD: nexuspay_dev
    volumes:
      - grafana_data:/var/lib/grafana
      - ./monitoring/grafana/datasources.yml:/etc/grafana/provisioning/datasources/datasources.yml:ro
      - ./monitoring/grafana/dashboards.yml:/etc/grafana/provisioning/dashboards/dashboards.yml:ro
      - ./monitoring/grafana/dashboards:/var/lib/grafana/dashboards:ro
    depends_on:
      - prometheus
    networks:
      - nexuspay-network

  # Jaeger for distributed tracing
  jaeger:
    image: jaegertracing/all-in-one:latest
    container_name: nexus-jaeger
    ports:
      - "16686:16686"
      - "14268:14268"
    environment:
      COLLECTOR_OTLP_ENABLED: true
    networks:
      - nexuspay-network

volumes:
  postgres_data:
  mongo_data:
  kafka_data:
  zk_data:
  zk_logs:
  redis_data:
  minio_data:
  prometheus_data:
  grafana_data:

networks:
  nexuspay-network:
    driver: bridge
```

## Database Initialization Scripts

### PostgreSQL Multi-Database Setup (infra/scripts/init-databases.sh)

```bash
#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    -- Create databases for each service
    CREATE DATABASE billing;
    CREATE DATABASE identity;  
    CREATE DATABASE metering;
    
    -- Create dedicated users for each service
    CREATE USER billing_user WITH PASSWORD 'billing_pass';
    CREATE USER identity_user WITH PASSWORD 'identity_pass';
    CREATE USER metering_user WITH PASSWORD 'metering_pass';
    
    -- Grant privileges
    GRANT ALL PRIVILEGES ON DATABASE billing TO billing_user;
    GRANT ALL PRIVILEGES ON DATABASE identity TO identity_user;
    GRANT ALL PRIVILEGES ON DATABASE metering TO metering_user;
    
    -- Create schemas
    \\c billing
    CREATE SCHEMA IF NOT EXISTS billing;
    CREATE SCHEMA IF NOT EXISTS audit;
    GRANT ALL ON SCHEMA billing TO billing_user;
    GRANT ALL ON SCHEMA audit TO billing_user;
    
    \\c identity
    CREATE SCHEMA IF NOT EXISTS identity;
    GRANT ALL ON SCHEMA identity TO identity_user;
    
    \\c metering
    CREATE SCHEMA IF NOT EXISTS metering;
    GRANT ALL ON SCHEMA metering TO metering_user;
EOSQL

echo "PostgreSQL databases initialized successfully!"
```

### MongoDB Initialization (infra/scripts/init-mongo.js)

```javascript
// Initialize MongoDB for NexusPay
db = db.getSiblingDB('events');

// Create collections with proper indexing
db.createCollection('usage_events');
db.createCollection('billing_events');
db.createCollection('audit_events');

// Create indexes for optimal query performance
db.usage_events.createIndex({ "tenantId": 1, "timestamp": -1 });
db.usage_events.createIndex({ "tenantId": 1, "customerId": 1, "timestamp": -1 });
db.usage_events.createIndex({ "eventType": 1, "timestamp": -1 });
db.usage_events.createIndex({ "processed": 1, "timestamp": 1 });

db.billing_events.createIndex({ "tenantId": 1, "timestamp": -1 });
db.billing_events.createIndex({ "eventType": 1, "timestamp": -1 });
db.billing_events.createIndex({ "aggregationId": 1 });

db.audit_events.createIndex({ "tenantId": 1, "timestamp": -1 });
db.audit_events.createIndex({ "userId": 1, "timestamp": -1 });
db.audit_events.createIndex({ "action": 1, "timestamp": -1 });

// Create users for services
db.createUser({
  user: "metering_service",
  pwd: "metering_pass",
  roles: [
    { role: "readWrite", db: "events" }
  ]
});

db.createUser({
  user: "billing_service",
  pwd: "billing_pass",
  roles: [
    { role: "readWrite", db: "events" }
  ]
});

print("MongoDB initialization completed!");
```

## Development Scripts

### Start Development Environment (scripts/dev-start.sh)

```bash
#!/bin/bash

echo "🚀 Starting NexusPay Development Environment..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Create necessary directories
mkdir -p logs
mkdir -p data/postgres
mkdir -p data/mongo
mkdir -p data/kafka
mkdir -p data/redis

# Start infrastructure services
echo "📦 Starting infrastructure services..."
docker-compose -f infra/docker-compose.yml up -d

# Wait for services to be ready
echo "⏳ Waiting for services to start..."

# Wait for PostgreSQL
until docker-compose -f infra/docker-compose.yml exec -T postgres pg_isready -U nexuspay > /dev/null 2>&1; do
    echo "Waiting for PostgreSQL..."
    sleep 2
done

# Wait for Kafka
until docker-compose -f infra/docker-compose.yml exec -T kafka kafka-topics --bootstrap-server localhost:9092 --list > /dev/null 2>&1; do
    echo "Waiting for Kafka..."
    sleep 2
done

# Create Kafka topics
echo "📋 Creating Kafka topics..."
docker-compose -f infra/docker-compose.yml exec kafka kafka-topics --create --topic billing-events --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1 --if-not-exists
docker-compose -f infra/docker-compose.yml exec kafka kafka-topics --create --topic usage-events --bootstrap-server localhost:9092 --partitions 6 --replication-factor 1 --if-not-exists
docker-compose -f infra/docker-compose.yml exec kafka kafka-topics --create --topic payment-events --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1 --if-not-exists
docker-compose -f infra/docker-compose.yml exec kafka kafka-topics --create --topic notification-events --bootstrap-server localhost:9092 --partitions 2 --replication-factor 1 --if-not-exists

echo "✅ Infrastructure services are ready!"
echo ""
echo "🌐 Service URLs:"
echo "   PostgreSQL:    localhost:5432"
echo "   MongoDB:       localhost:27017"
echo "   Kafka:         localhost:9092"
echo "   Redis:         localhost:6379"
echo "   Kafka UI:      http://localhost:8090"
echo "   MinIO:         http://localhost:9001 (admin: nexuspay/nexuspay_dev123)"
echo "   Prometheus:    http://localhost:9090"
echo "   Grafana:       http://localhost:3001 (admin: admin/nexuspay_dev)"
echo "   Jaeger:        http://localhost:16686"
echo ""
echo "📚 Next steps:"
echo "   1. Start the backend services: ./scripts/start-backend.sh"
echo "   2. Start the frontend: ./scripts/start-frontend.sh"
echo "   3. Run database migrations: ./scripts/migrate.sh"
```

### Backend Services Starter (scripts/start-backend.sh)

```bash
#!/bin/bash

echo "🎯 Starting NexusPay Backend Services..."

# Set JAVA_HOME if not set
if [ -z "$JAVA_HOME" ]; then
    echo "⚠️  JAVA_HOME is not set. Attempting to find Java 21..."
    if command -v java >/dev/null 2>&1; then
        JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
        if [ "$JAVA_VERSION" -ge 21 ]; then
            echo "✅ Found Java $JAVA_VERSION"
        else
            echo "❌ Java 21+ is required. Found Java $JAVA_VERSION"
            exit 1
        fi
    else
        echo "❌ Java not found. Please install Java 21+"
        exit 1
    fi
fi

# Function to start a service
start_service() {
    local service_name=$1
    local service_path="backend/$service_name"
    local port=$2
    
    echo "🚀 Starting $service_name on port $port..."
    
    cd $service_path
    
    # Clean and compile if needed
    if [ ! -d "target" ] || [ "pom.xml" -nt "target" ]; then
        echo "📦 Building $service_name..."
        mvn clean compile -q
    fi
    
    # Start in background
    mvn spring-boot:run -Dspring-boot.run.jvmArguments="-server.port=$port" > "../../logs/$service_name.log" 2>&1 &
    echo $! > "../../logs/$service_name.pid"
    
    cd - > /dev/null
    echo "✅ $service_name started (PID: $(cat logs/$service_name.pid), Port: $port)"
}

# Create logs directory
mkdir -p logs

# Start services in dependency order
start_service "nexus-identity-service" 8081
sleep 5

start_service "nexus-api-gateway" 8080
sleep 3

start_service "nexus-metering-service" 8082
sleep 3

start_service "nexus-billing-service" 8083
sleep 3

start_service "nexus-ml-service" 8084

echo ""
echo "✅ All backend services started!"
echo ""
echo "🌐 Service endpoints:"
echo "   API Gateway:      http://localhost:8080"
echo "   Identity Service: http://localhost:8081"
echo "   Metering Service: http://localhost:8082"  
echo "   Billing Service:  http://localhost:8083"
echo "   ML Service:       http://localhost:8084"
echo ""
echo "📊 Health checks:"
echo "   curl http://localhost:8080/actuator/health"
echo ""
echo "📝 Logs are available in the logs/ directory"
echo "🛑 To stop services: ./scripts/stop-backend.sh"
```

### Frontend Starter (scripts/start-frontend.sh)

```bash
#!/bin/bash

echo "💻 Starting NexusPay Frontend..."

# Check if Node.js is installed
if ! command -v node >/dev/null 2>&1; then
    echo "❌ Node.js is not installed. Please install Node.js 18+"
    exit 1
fi

# Check Node.js version
NODE_VERSION=$(node -v | cut -d'v' -f2 | cut -d'.' -f1)
if [ "$NODE_VERSION" -lt 18 ]; then
    echo "❌ Node.js 18+ is required. Found version: $(node -v)"
    exit 1
fi

cd frontend

# Install dependencies if needed
if [ ! -d "node_modules" ] || [ "package.json" -nt "node_modules" ]; then
    echo "📦 Installing dependencies..."
    npm install
fi

# Set environment variables for development
export VITE_API_BASE_URL=http://localhost:8080/api/v1
export VITE_WS_URL=ws://localhost:8080/ws

echo "🚀 Starting development server..."
npm run dev

cd - > /dev/null
```

### Stop All Services (scripts/stop-all.sh)

```bash
#!/bin/bash

echo "🛑 Stopping NexusPay Development Environment..."

# Stop backend services
if [ -f "scripts/stop-backend.sh" ]; then
    ./scripts/stop-backend.sh
fi

# Stop infrastructure
echo "📦 Stopping infrastructure services..."
docker-compose -f infra/docker-compose.yml down

echo "✅ All services stopped!"
```

## Monitoring Configuration

### Prometheus Configuration (infra/monitoring/prometheus.yml)

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'nexus-api-gateway'
    static_configs:
      - targets: ['host.docker.internal:8080']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 15s

  - job_name: 'nexus-identity-service'
    static_configs:
      - targets: ['host.docker.internal:8081']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 15s

  - job_name: 'nexus-metering-service'
    static_configs:
      - targets: ['host.docker.internal:8082']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 15s

  - job_name: 'nexus-billing-service'
    static_configs:
      - targets: ['host.docker.internal:8083']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 15s

  - job_name: 'nexus-ml-service'
    static_configs:
      - targets: ['host.docker.internal:8084']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 15s

  - job_name: 'postgres-exporter'
    static_configs:
      - targets: ['postgres-exporter:9187']

  - job_name: 'kafka-exporter'
    static_configs:
      - targets: ['kafka-exporter:9308']

  - job_name: 'redis-exporter'
    static_configs:
      - targets: ['redis-exporter:9121']
```

## Quick Start Guide

```bash
# 1. Clone the repository
git clone https://github.com/Adarsh-P-Thomson/NexusPayments.git
cd NexusPayments

# 2. Make scripts executable
chmod +x scripts/*.sh

# 3. Start development environment
./scripts/dev-start.sh

# 4. In a new terminal, start backend services
./scripts/start-backend.sh

# 5. In another terminal, start frontend
./scripts/start-frontend.sh

# 6. Access the application
open http://localhost:3000
```

This infrastructure setup provides:

1. **Complete local development environment** with all dependencies
2. **Automated service orchestration** with proper startup order
3. **Comprehensive monitoring** with Prometheus, Grafana, and Jaeger
4. **Event streaming** with Kafka for real-time processing
5. **Database setup** with PostgreSQL for transactions and MongoDB for events
6. **Easy scripts** for starting/stopping the entire stack
7. **Production-ready monitoring** and observability tools