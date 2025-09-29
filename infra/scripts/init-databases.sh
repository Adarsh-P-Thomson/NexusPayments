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