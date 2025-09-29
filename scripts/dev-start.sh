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