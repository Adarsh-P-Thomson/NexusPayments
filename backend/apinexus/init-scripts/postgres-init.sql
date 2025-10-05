-- PostgreSQL Initialization Script for NexusPay
-- This script runs automatically when the Docker container is first created

-- Wait for the database to be fully ready
SELECT pg_sleep(1);

-- Note: This script runs BEFORE the Spring Boot application starts
-- Since we use JPA with ddl-auto=update, JPA will create tables automatically
-- This script only creates sample data after tables are created by JPA

-- The init script will be executed, but actual data insertion 
-- will be handled by the Spring Boot DataInitController at /api/init/data
-- when the application starts

-- For now, we just ensure the database is ready
SELECT 'PostgreSQL initialization script executed - database ready' AS status;

