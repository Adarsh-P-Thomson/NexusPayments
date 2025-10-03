#!/bin/bash

# NexusPay Startup Script
# This script starts all required services for NexusPay

set -e

echo "🚀 Starting NexusPay..."
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check prerequisites
echo "📋 Checking prerequisites..."

if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Java is not installed. Please install Java 17 or higher.${NC}"
    exit 1
fi

if ! command -v mvn &> /dev/null; then
    echo -e "${RED}❌ Maven is not installed. Please install Maven 3.6 or higher.${NC}"
    exit 1
fi

if ! command -v node &> /dev/null; then
    echo -e "${RED}❌ Node.js is not installed. Please install Node.js 16 or higher.${NC}"
    exit 1
fi

if ! command -v docker &> /dev/null; then
    echo -e "${YELLOW}⚠️  Docker is not installed. You'll need to manually set up PostgreSQL and MongoDB.${NC}"
    DOCKER_AVAILABLE=false
else
    DOCKER_AVAILABLE=true
fi

echo -e "${GREEN}✅ All prerequisites met!${NC}"
echo ""

# Start databases
echo "🗄️  Starting databases..."
if [ "$DOCKER_AVAILABLE" = true ]; then
    cd backend/apinexus
    docker-compose up -d
    cd ../..
    echo -e "${GREEN}✅ Databases started with Docker${NC}"
else
    echo -e "${YELLOW}⚠️  Please ensure PostgreSQL and MongoDB are running manually${NC}"
fi
echo ""

# Build and start backend
echo "🔧 Building backend..."
cd backend/apinexus
mvn clean package -DskipTests -q
echo -e "${GREEN}✅ Backend built successfully${NC}"
echo ""

echo "🚀 Starting backend server..."
mvn spring-boot:run &
BACKEND_PID=$!
cd ../..
echo -e "${GREEN}✅ Backend starting on http://localhost:8080${NC}"
echo ""

# Install frontend dependencies and start
echo "📦 Installing frontend dependencies..."
cd frontend/nexus
if [ ! -d "node_modules" ]; then
    npm install
fi
echo -e "${GREEN}✅ Frontend dependencies installed${NC}"
echo ""

echo "🎨 Starting frontend server..."
npm run dev &
FRONTEND_PID=$!
cd ../..
echo -e "${GREEN}✅ Frontend starting on http://localhost:5173${NC}"
echo ""

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${GREEN}🎉 NexusPay is starting!${NC}"
echo ""
echo "📍 Services:"
echo "   • Frontend:  http://localhost:5173"
echo "   • Backend:   http://localhost:8080"
if [ "$DOCKER_AVAILABLE" = true ]; then
    echo "   • PostgreSQL: localhost:5432"
    echo "   • MongoDB:    localhost:27017"
fi
echo ""
echo "📚 Next Steps:"
echo "   1. Open http://localhost:5173 in your browser"
echo "   2. Navigate to 'Initialize Data' to set up sample data"
echo "   3. Explore the subscription and payment features!"
echo ""
echo "🛑 To stop all services, press Ctrl+C"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Wait for user to stop
trap "echo ''; echo '🛑 Stopping services...'; kill $BACKEND_PID $FRONTEND_PID 2>/dev/null; if [ '$DOCKER_AVAILABLE' = true ]; then cd backend/apinexus && docker-compose down; fi; echo '✅ All services stopped'; exit 0" INT

wait
