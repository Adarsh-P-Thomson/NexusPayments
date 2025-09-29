#!/bin/bash

echo "🛑 Stopping NexusPay Backend Services..."

# Function to stop a service
stop_service() {
    local service_name=$1
    local pid_file="logs/$service_name.pid"
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if kill -0 "$pid" 2>/dev/null; then
            echo "🛑 Stopping $service_name (PID: $pid)"
            kill "$pid"
            rm "$pid_file"
        else
            echo "⚠️  $service_name was not running"
            rm "$pid_file"
        fi
    else
        echo "⚠️  No PID file found for $service_name"
    fi
}

# Stop services in reverse order
stop_service "nexus-ml-service"
stop_service "nexus-billing-service"
stop_service "nexus-metering-service"
stop_service "nexus-api-gateway"
stop_service "nexus-identity-service"

echo "✅ All backend services stopped!"