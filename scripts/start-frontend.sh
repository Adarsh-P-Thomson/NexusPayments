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