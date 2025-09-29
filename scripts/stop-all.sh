#!/bin/bash

echo "🛑 Stopping all NexusPay services..."

# Stop backend services
./scripts/stop-backend.sh

# Stop infrastructure services
echo "🛑 Stopping infrastructure services..."
docker-compose -f infra/docker-compose.yml down

echo "✅ All services stopped!"