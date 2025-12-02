# Models (Entities)

Models represent database tables. Each model is a Java class that maps to a table in PostgreSQL or a collection in MongoDB.

## 📄 Files

### `User.java`
**Database**: PostgreSQL  
**Purpose**: Stores user account information
- User ID
- Email address
- Name
- Creation and update timestamps

### `CardDetail.java`
**Database**: PostgreSQL  
**Purpose**: Stores saved payment card information
- Card ID
- User ID (who owns the card)
- Cardholder name
- Last 4 digits of card number
- Card type (Visa, Mastercard, etc.)
- Expiry month and year

### `SubscriptionPlan.java`
**Database**: PostgreSQL  
**Purpose**: Defines available subscription tiers
- Plan ID
- Plan name (Basic, Professional, Enterprise)
- Monthly and yearly prices
- Features included
- Active/inactive status

### `UserSubscription.java`
**Database**: PostgreSQL  
**Purpose**: Tracks which users are subscribed to which plans
- Subscription ID
- User ID
- Plan ID
- Status (ACTIVE, CANCELLED, EXPIRED)
- Billing cycle (MONTHLY, YEARLY)
- Start date, end date, next billing date

### `Bill.java`
**Database**: PostgreSQL  
**Purpose**: Stores billing information for subscriptions
- Bill ID
- User ID
- Subscription ID
- Amount due
- Due date
- Status (PENDING, PAID, OVERDUE)

### `Transaction.java`
**Database**: MongoDB  
**Purpose**: Records all payment transactions (flexible schema for analytics)
- Transaction ID
- User ID
- Bill ID
- Amount
- Payment status (SUCCESS, FAILED)
- Payment method
- Failure reason (if failed)
- Retry count
- Metadata (card details, gateway response)

### `Sale.java`
**Database**: PostgreSQL  
**Purpose**: Stores sales/purchase records
- Sale ID
- Date
- Total amount
- Items sold
- Customer information

### `Purchase.java`
**Database**: MongoDB  
**Purpose**: Detailed purchase records with flexible schema
- Purchase details
- Product information
- Analytics data
- Can store complex nested data
