# Utilities

Utility classes provide helper functions and tools used throughout the application for common tasks.

## 📄 Files

### `DatabaseInitializer.java`
**Purpose**: Main database setup coordinator  
**What it does**:
- Runs on application startup
- Checks if databases are accessible
- Calls other initializers in correct order
- Ensures tables and collections exist
- Reports any setup errors

### `SchemaInitializer.java`
**Purpose**: PostgreSQL schema setup  
**What it does**:
- Creates all required tables if they don't exist
- Sets up foreign key relationships
- Creates indexes for faster queries
- Ensures data integrity constraints

### `MongoDataInitializer.java`
**Purpose**: MongoDB data population  
**What it does**:
- Creates sample transaction documents
- Generates test purchase records
- Populates collections with varied data
- Useful for testing MongoDB queries

### `SubscriptionPlanInitializer.java`
**Purpose**: Default subscription plan creation  
**What it does**:
- Creates Basic plan ($9.99/month, $99.99/year)
- Creates Professional plan ($29.99/month, $299.99/year)
- Creates Enterprise plan ($99.99/month, $999.99/year)
- Sets up feature lists for each plan
- Only runs if no plans exist (won't duplicate)

### `VerifySchema.java`
**Purpose**: Database validation tool  
**What it does**:
- Checks if all required tables exist
- Validates table structures match models
- Reports missing tables or columns
- Helps debug database issues

### `VerifyMongoPurchases.java`
**Purpose**: MongoDB data validation  
**What it does**:
- Counts purchase documents
- Checks data format is correct
- Validates required fields exist
- Reports data quality issues

### `RunMigration.java`
**Purpose**: Database migration manager  
**What it does**:
- Updates database schema safely
- Migrates existing data to new structure
- Tracks which migrations have run
- Prevents running same migration twice

### `ClearSalesData.java`
**Purpose**: Data cleanup utility  
**What it does**:
- Removes all sales records
- Clears purchase data
- Resets analytics
- Useful for testing fresh data generation

### `ExpandSalesData.java`
**Purpose**: Test data generator  
**What it does**:
- Creates large volumes of sales data
- Generates realistic purchase patterns
- Adds date variety (past year of data)
- Useful for performance testing
