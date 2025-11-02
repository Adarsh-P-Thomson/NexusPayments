# Database Setup Guide

## PostgreSQL Setup

### Manual Setup (Recommended)
1. Open **pgAdmin** or **psql** terminal
2. Connect to PostgreSQL with user `postgres` and password `root`
3. Run the following SQL command:

```sql
CREATE DATABASE nexuspay;
```

### Using psql command line:
```bash
# If psql is in your PATH
psql -U postgres
# Enter password: root

# Then run:
CREATE DATABASE nexuspay;
\q
```

### Using pgAdmin:
1. Right-click on "Databases" in the left sidebar
2. Select "Create" > "Database..."
3. Enter database name: `nexuspay`
4. Click "Save"

## MongoDB Setup

MongoDB database will be created automatically when the application first connects.

### Verify MongoDB is running:
1. Open **MongoDB Compass** or use command line:
```bash
mongosh
show dbs
```

2. The `nexuspay` database will appear after the first application run.

## Connection Details

### PostgreSQL:
- Host: localhost
- Port: 5432
- Database: nexuspay
- Username: postgres
- Password: root

### MongoDB:
- Host: localhost
- Port: 27017
- Database: nexuspay
- No authentication required

## Verification

After creating the PostgreSQL database, run the Spring Boot application:

```bash
mvnw spring-boot:run
```

Look for these messages in the console:
```
✓ PostgreSQL Connection: SUCCESS
✓ MongoDB Connection: SUCCESS
```

If you see these messages, your databases are configured correctly!
