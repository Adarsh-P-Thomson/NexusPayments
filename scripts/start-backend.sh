#!/bin/bash

echo "🎯 Starting NexusPay Backend Services..."

# Set JAVA_HOME if not set
if [ -z "$JAVA_HOME" ]; then
    echo "⚠️  JAVA_HOME is not set. Attempting to find Java 17..."
    if command -v java >/dev/null 2>&1; then
        JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
        if [ "$JAVA_VERSION" -ge 17 ]; then
            echo "✅ Found Java $JAVA_VERSION"
        else
            echo "❌ Java 17+ is required. Found Java $JAVA_VERSION"
            exit 1
        fi
    else
        echo "❌ Java not found. Please install Java 17+"
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