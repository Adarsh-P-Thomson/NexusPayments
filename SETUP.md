# NexusPay Setup Guide

This guide walks you through setting up the complete NexusPay development environment.

## Prerequisites

- **Docker**: Version 20.10 or higher
- **Java**: OpenJDK 17 or higher
- **Maven**: Version 3.6 or higher
- **Node.js**: Version 18 or higher
- **npm**: Version 8 or higher

## Quick Start

### 1. Start Infrastructure Services

```bash
# Make scripts executable
chmod +x scripts/*.sh

# Start all infrastructure services (PostgreSQL, MongoDB, Kafka, Redis, etc.)
./scripts/dev-start.sh
```

This will start:
- PostgreSQL (port 5432) - Transactional database
- MongoDB (port 27017) - Event storage
- Apache Kafka (port 9092) - Event streaming
- Redis (port 6379) - Caching
- Kafka UI (port 8090) - Kafka management interface
- MinIO (port 9001) - Object storage
- Prometheus (port 9090) - Metrics collection
- Grafana (port 3001) - Monitoring dashboards
- Jaeger (port 16686) - Distributed tracing

### 2. Start Backend Services

```bash
# Start backend microservices
./scripts/start-backend.sh
```

This will start:
- Identity Service (port 8081)

### 3. Start Frontend

```bash
# Start the React frontend
./scripts/start-frontend.sh
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