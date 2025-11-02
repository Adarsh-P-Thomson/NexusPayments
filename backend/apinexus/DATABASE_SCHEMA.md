# ✅ NexusPay Database Schema - CREATED SUCCESSFULLY!

## Database Information
- **Database Name:** `nexuspay`
- **Host:** `localhost:5432`
- **Username:** `postgres`
- **Password:** `root`

## Tables Created

### 1. `analytics_login` 
**Purpose:** Store login credentials for analytics site (unencrypted for project use)

| Column | Type | Description |
|--------|------|-------------|
| id | BIGSERIAL | Primary key |
| username | VARCHAR(100) | Unique username |
| password | VARCHAR(255) | Plain text password (project only) |
| email | VARCHAR(255) | Unique email |
| is_active | BOOLEAN | Account status (default: true) |
| last_login | TIMESTAMP | Last login timestamp |
| created_at | TIMESTAMP | Account creation time |
| updated_at | TIMESTAMP | Last update time |

**Sample Data:**
- `admin` / `admin123` / admin@nexuspay.com
- `analyst` / `analyst123` / analyst@nexuspay.com

---

### 2. `user_details`
**Purpose:** Store user information and login passwords with premium/normal classification

| Column | Type | Description |
|--------|------|-------------|
| id | BIGSERIAL | Primary key |
| username | VARCHAR(100) | Unique username |
| password | VARCHAR(255) | Plain text password (project only) |
| email | VARCHAR(255) | Unique email |
| full_name | VARCHAR(200) | User's full name |
| phone_number | VARCHAR(20) | Contact number |
| is_premium | BOOLEAN | Premium user flag (default: false) |
| is_active | BOOLEAN | Account status (default: true) |
| last_login | TIMESTAMP | Last login timestamp |
| created_at | TIMESTAMP | Account creation time |
| updated_at | TIMESTAMP | Last update time |

**Sample Data:**
- `john_doe` (Normal user)
- `jane_smith` (Premium user)
- `premium_user` (Premium user)
- `normal_user` (Normal user)

---

### 3. `market_items`
**Purpose:** Store market items with dual pricing for premium and normal users

| Column | Type | Description |
|--------|------|-------------|
| id | BIGSERIAL | Primary key |
| item_name | VARCHAR(255) | Product name |
| description | TEXT | Product description |
| category | VARCHAR(100) | Product category |
| normal_price | DECIMAL(10,2) | Price for normal users |
| premium_price | DECIMAL(10,2) | Discounted price for premium users |
| stock_quantity | INTEGER | Available stock (default: 0) |
| image_url | VARCHAR(500) | Product image URL |
| is_available | BOOLEAN | Availability status (default: true) |
| created_at | TIMESTAMP | Item creation time |
| updated_at | TIMESTAMP | Last update time |

**Sample Data (10 items):**
1. Premium Coffee Beans - $24.99 / $19.99
2. Wireless Earbuds - $79.99 / $59.99
3. Yoga Mat - $34.99 / $27.99
4. Organic Green Tea - $15.99 / $12.99
5. Smart Watch - $199.99 / $149.99
6. Protein Powder - $49.99 / $39.99
7. Running Shoes - $89.99 / $69.99
8. Notebook Set - $29.99 / $24.99
9. Water Bottle - $19.99 / $15.99
10. Desk Lamp - $44.99 / $34.99

---

## Indexes Created (for Performance)

### analytics_login
- `idx_analytics_login_username` - On username column
- `idx_analytics_login_email` - On email column

### user_details
- `idx_user_details_username` - On username column
- `idx_user_details_email` - On email column
- `idx_user_details_is_premium` - On is_premium column

### market_items
- `idx_market_items_category` - On category column
- `idx_market_items_is_available` - On is_available column

---

## Key Features

✅ **Dual Pricing System** - Automatic price differentiation for premium vs normal users  
✅ **Analytics Login** - Separate authentication for analytics dashboard  
✅ **User Management** - Complete user profile with premium tier support  
✅ **Inventory Tracking** - Stock quantity management for each item  
✅ **Categories** - Organized product categorization  
✅ **Timestamps** - Automatic tracking of creation and update times  
✅ **Active Status** - Soft delete capability with is_active flags  

---

## Pricing Benefits by User Type

| User Type | Discount | Example Savings |
|-----------|----------|-----------------|
| **Normal User** | 0% | Pays full price ($24.99) |
| **Premium User** | ~20% | Saves ~$5 per item ($19.99) |

---

## Database Utilities Created

1. **SchemaInitializer.java** - Creates all tables and inserts sample data
   - Location: `src/main/java/com/apiserver/apinexus/util/SchemaInitializer.java`
   - Run: `mvnw compile exec:java "-Dexec.mainClass=com.apiserver.apinexus.util.SchemaInitializer"`

2. **VerifySchema.java** - Displays all table data in formatted output
   - Location: `src/main/java/com/apiserver/apinexus/util/VerifySchema.java`
   - Run: `mvnw compile exec:java "-Dexec.mainClass=com.apiserver.apinexus.util.VerifySchema"`

---

## SQL Schema File
- Location: `src/main/resources/schema.sql`
- Contains all CREATE TABLE, CREATE INDEX, and INSERT statements

---

## Next Steps

1. ✅ Database schema created
2. ✅ Sample data inserted
3. ✅ Indexes optimized
4. 🎯 Create JPA Entity classes for these tables
5. 🎯 Create Repository interfaces
6. 🎯 Create Service layer for business logic
7. 🎯 Create REST API controllers
8. 🎯 Implement authentication/authorization
9. 🎯 Build frontend UI for market and analytics

---

**Status:** Database ready for application development! 🚀
