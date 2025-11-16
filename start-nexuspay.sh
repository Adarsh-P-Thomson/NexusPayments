#!/bin/bash

# NexusPay Complete Startup Script
# This script starts all services for NexusPay

set -e

echo "🚀 NexusPay Startup Script"
echo "=========================="
echo ""

# Get the script directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$SCRIPT_DIR"

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}✓${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

print_info() {
    echo -e "${YELLOW}ℹ${NC} $1"
}

# Check prerequisites
echo "Checking prerequisites..."

# Check PostgreSQL
if ! command -v psql &> /dev/null; then
    print_error "PostgreSQL is not installed. Please install PostgreSQL 12+ first."
    exit 1
fi
print_status "PostgreSQL is installed"

# Check MongoDB
if ! command -v mongosh &> /dev/null && ! command -v mongo &> /dev/null; then
    print_error "MongoDB is not installed. Please install MongoDB 4.4+ first."
    exit 1
fi
print_status "MongoDB is installed"

# Check Java
if ! command -v java &> /dev/null; then
    print_error "Java is not installed. Please install Java 17+ first."
    exit 1
fi
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    print_error "Java 17+ is required. Current version: $JAVA_VERSION"
    exit 1
fi
print_status "Java $JAVA_VERSION is installed"

# Check Maven
if ! command -v mvn &> /dev/null; then
    print_error "Maven is not installed. Please install Maven 3.6+ first."
    exit 1
fi
print_status "Maven is installed"

# Check Node.js
if ! command -v node &> /dev/null; then
    print_error "Node.js is not installed. Please install Node.js 18+ first."
    exit 1
fi
print_status "Node.js is installed"

# Check npm
if ! command -v npm &> /dev/null; then
    print_error "npm is not installed. Please install npm first."
    exit 1
fi
print_status "npm is installed"

echo ""
echo "Checking database connectivity..."
echo ""

# Check PostgreSQL connection
print_info "Checking PostgreSQL connection..."
if psql -U nexuspay -d nexuspay -h localhost -c "SELECT 1" &> /dev/null; then
    print_status "PostgreSQL is accessible"
else
    print_error "Cannot connect to PostgreSQL. Please ensure:"
    echo "  1. PostgreSQL is running (e.g., brew services start postgresql or sudo systemctl start postgresql)"
    echo "  2. Database 'nexuspay' exists"
    echo "  3. User 'nexuspay' exists with password 'nexuspay_dev'"
    echo ""
    echo "To setup, run:"
    echo "  psql -U postgres"
    echo "  CREATE DATABASE nexuspay;"
    echo "  CREATE USER nexuspay WITH PASSWORD 'nexuspay_dev';"
    echo "  GRANT ALL PRIVILEGES ON DATABASE nexuspay TO nexuspay;"
    exit 1
fi

# Check MongoDB connection
print_info "Checking MongoDB connection..."
if command -v mongosh &> /dev/null; then
    MONGO_CMD="mongosh"
else
    MONGO_CMD="mongo"
fi

if $MONGO_CMD "mongodb://nexuspay:nexuspay_dev@localhost:27017/nexuspay_transactions" --eval "db.version()" &> /dev/null; then
    print_status "MongoDB is accessible"
else
    print_error "Cannot connect to MongoDB. Please ensure:"
    echo "  1. MongoDB is running (e.g., brew services start mongodb-community or sudo systemctl start mongod)"
    echo "  2. User 'nexuspay' exists with access to 'nexuspay_transactions' database"
    echo ""
    echo "To setup, run:"
    echo "  $MONGO_CMD"
    echo "  use admin"
    echo "  db.createUser({user: 'nexuspay', pwd: 'nexuspay_dev', roles: [{role: 'readWrite', db: 'nexuspay_transactions'}]})"
    exit 1
fi

echo ""
echo "Starting NexusPay services..."
echo ""
else
    print_error "Databases failed to start properly. Check 'docker ps' for status."
    exit 1
fi

echo ""

# 2. Start Backend
print_info "Starting backend API server..."
cd "$PROJECT_ROOT/backend/apinexus"

# Start backend in background
mvn spring-boot:run > "$PROJECT_ROOT/backend.log" 2>&1 &
BACKEND_PID=$!

# Wait for backend to start
print_info "Waiting for backend to start (this may take 30-60 seconds)..."
for i in {1..60}; do
    if curl -s http://localhost:8080/api/health > /dev/null 2>&1; then
        print_status "Backend is running on http://localhost:8080"
        break
    fi
    if [ $i -eq 60 ]; then
        print_error "Backend failed to start. Check backend.log for details."
        kill $BACKEND_PID 2>/dev/null || true
        exit 1
    fi
    sleep 2
done

# 3. Initialize Data
print_info "Initializing sample data..."
INIT_RESPONSE=$(curl -s -X POST http://localhost:8080/api/init/data)
if echo "$INIT_RESPONSE" | grep -q '"success":true'; then
    print_status "Sample data initialized successfully"
else
    print_info "Data may already be initialized or initialization failed. Continuing..."
fi

echo ""

# 4. Start Frontend
print_info "Starting frontend..."
cd "$PROJECT_ROOT/frontend/nexus"

# Install dependencies if needed
if [ ! -d "node_modules" ]; then
    print_info "Installing frontend dependencies..."
    npm install
fi

# Start frontend in background
npm run dev > "$PROJECT_ROOT/frontend.log" 2>&1 &
FRONTEND_PID=$!

# Wait for frontend to start
print_info "Waiting for frontend to start..."
for i in {1..30}; do
    if curl -s http://localhost:5173 > /dev/null 2>&1; then
        print_status "Frontend is running on http://localhost:5173"
        break
    fi
    if [ $i -eq 30 ]; then
        print_error "Frontend failed to start. Check frontend.log for details."
        kill $BACKEND_PID 2>/dev/null || true
        kill $FRONTEND_PID 2>/dev/null || true
        exit 1
    fi
    sleep 2
done

echo ""
echo "=========================================="
echo -e "${GREEN}✓ NexusPay is now running!${NC}"
echo "=========================================="
echo ""
echo "📊 Services:"
echo "  - Frontend:    http://localhost:5173"
echo "  - Backend API: http://localhost:8080/api"
echo "  - Health:      http://localhost:8080/api/health"
echo ""
echo "📝 Logs:"
echo "  - Backend:  $PROJECT_ROOT/backend.log"
echo "  - Frontend: $PROJECT_ROOT/frontend.log"
echo ""
echo "🛑 To stop all services:"
echo "  1. Press Ctrl+C to stop this script"
echo "  2. Run: cd backend/apinexus && docker compose down"
echo ""
echo "🌐 Open http://localhost:5173 in your browser to get started!"
echo ""

# Save PIDs for cleanup
echo $BACKEND_PID > "$PROJECT_ROOT/.backend.pid"
echo $FRONTEND_PID > "$PROJECT_ROOT/.frontend.pid"

# Wait for Ctrl+C
print_info "Press Ctrl+C to stop all services..."
trap cleanup EXIT

cleanup() {
    echo ""
    print_info "Stopping services..."
    
    # Kill backend
    if [ -f "$PROJECT_ROOT/.backend.pid" ]; then
        BACKEND_PID=$(cat "$PROJECT_ROOT/.backend.pid")
        kill $BACKEND_PID 2>/dev/null || true
        rm "$PROJECT_ROOT/.backend.pid"
    fi
    
    # Kill frontend
    if [ -f "$PROJECT_ROOT/.frontend.pid" ]; then
        FRONTEND_PID=$(cat "$PROJECT_ROOT/.frontend.pid")
        kill $FRONTEND_PID 2>/dev/null || true
        rm "$PROJECT_ROOT/.frontend.pid"
    fi
    
    print_status "Services stopped"
    echo ""
    print_info "To stop databases, run: cd backend/apinexus && docker compose down"
}

# Keep script running
wait
