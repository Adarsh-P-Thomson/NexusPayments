# Initializers

Initializers populate the database with sample data when the application starts. Useful for development and testing.

## 📄 Files

### `SalesDataInitializer.java`
**Purpose**: Creates sample sales records  
**What it does**:
- Generates random sales data for testing
- Creates sales across different dates
- Populates products and categories
- Useful for testing analytics features

### `MarketItemsExpander.java`
**Purpose**: Expands the product catalog  
**What it does**:
- Adds more product varieties
- Creates diverse product categories
- Generates realistic product data
- Helps test product recommendations

### Database Utility Scripts

These files in the `util/` subfolder help manage and verify data:

#### `DatabaseInitializer.java`
- Main database setup coordinator
- Calls other initializers in the right order
- Ensures database is ready before app starts

#### `SchemaInitializer.java`
- Creates database tables if they don't exist
- Sets up table relationships
- Initializes indexes for performance

#### `MongoDataInitializer.java`
- Populates MongoDB with sample transactions
- Creates test purchase records
- Generates sample analytics data

#### `SubscriptionPlanInitializer.java`
- Creates default subscription plans (Basic, Pro, Enterprise)
- Sets standard pricing
- Defines feature sets for each plan

#### `VerifySchema.java`
- Checks if database tables exist
- Validates table structure
- Reports any schema issues

#### `VerifyMongoPurchases.java`
- Validates MongoDB purchase data
- Checks data integrity
- Reports any data inconsistencies

#### `RunMigration.java`
- Handles database schema updates
- Migrates data when structure changes
- Safely updates existing data

#### `ClearSalesData.java`
- Removes all sales data
- Useful for resetting test environment
- Cleans up before fresh data generation

#### `ExpandSalesData.java`
- Generates more sales records
- Adds variety to existing data
- Useful for performance testing with large datasets
