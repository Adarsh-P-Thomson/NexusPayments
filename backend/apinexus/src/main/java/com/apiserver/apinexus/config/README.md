# Configuration

Configuration classes set up how the application connects to external services and databases.

## 📄 Files

### `DatabaseConfig.java`
**Purpose**: Configures database connections and settings

**What it does**:
- Sets up PostgreSQL connection pool
  - Connection URL: `jdbc:postgresql://localhost:5432/nexuspay`
  - Username and password from application.properties
  - Connection pool size and timeout settings

- Configures MongoDB connection
  - Connection URI: `mongodb://nexuspay:nexuspay_dev@localhost:27017/nexuspay_transactions`
  - Database name: `nexuspay_transactions`
  - Collection mappings

- Sets up JPA/Hibernate for PostgreSQL
  - Auto-create/update table schemas
  - SQL logging for debugging
  - PostgreSQL dialect

- Configures transaction management
  - Ensures data consistency
  - Rollback on errors
  - Handles both PostgreSQL and MongoDB transactions

**Why it's important**:
- Without this, the app can't talk to the databases
- Handles connection pooling for performance
- Manages database transactions safely
