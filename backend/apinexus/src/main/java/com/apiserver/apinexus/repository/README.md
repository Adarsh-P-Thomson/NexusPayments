# Repositories

Repositories handle database operations. They provide methods to create, read, update, and delete data from PostgreSQL and MongoDB.

## 📄 Files

### `UserRepository.java`
**Database**: PostgreSQL  
**Operations**: User account data access
- Find user by ID
- Find user by email
- Save new user
- Update user information
- Check if user exists

### `CardDetailRepository.java`
**Database**: PostgreSQL  
**Operations**: Payment card data access
- Find all cards for a user
- Find card by ID
- Save new card
- Delete card
- Update card details

### `SubscriptionPlanRepository.java`
**Database**: PostgreSQL  
**Operations**: Subscription plan data access
- Get all active plans
- Find plan by ID
- Find plan by name
- Save new plan
- Update plan
- Check if plan name already exists

### `UserSubscriptionRepository.java`
**Database**: PostgreSQL  
**Operations**: User subscription data access
- Find active subscription for a user
- Find all subscriptions for a user
- Find subscription by ID
- Save new subscription
- Update subscription status
- Check if user has active subscription

### `BillRepository.java`
**Database**: PostgreSQL  
**Operations**: Bill data access
- Find all bills for a user
- Find unpaid bills
- Find overdue bills
- Find bill by ID
- Save new bill
- Update bill status

### `TransactionRepository.java`
**Database**: MongoDB  
**Operations**: Transaction data access
- Find all transactions for a user
- Find transactions for a bill
- Find failed transactions
- Save new transaction
- Query transactions by date range
- Aggregate transaction statistics

### `SaleRepository.java`
**Database**: PostgreSQL  
**Operations**: Sales record data access
- Find sales by date
- Get all sales
- Save new sale
- Calculate total sales

### `PurchaseRepository.java`
**Database**: MongoDB  
**Operations**: Purchase data access (flexible schema)
- Find purchases by user
- Find purchases by date
- Save purchase details
- Query purchase analytics
- Store complex purchase metadata
