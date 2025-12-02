# Controllers

Controllers handle HTTP requests from the frontend and return responses. Each controller manages a specific feature area.

## 📄 Files

### `BillController.java`
Handles bill-related API endpoints:
- Get bills for a user
- View bill details
- Generate new bills for subscriptions

### `CardDetailController.java`
Handles payment card management:
- Add new payment cards
- View saved cards
- Update card details
- Delete cards

### `DataInitController.java`
Provides endpoints to initialize sample data:
- Create demo users
- Generate sample subscription plans
- Populate test transactions
- Useful for testing and development

### `HealthController.java`
Monitors system health:
- Check if PostgreSQL database is connected
- Check if MongoDB is connected
- Overall system health status

### `PaymentController.java`
Handles payment processing:
- Process subscription payments
- Handle payment retries for failed transactions
- Simulate payment gateway interactions

### `SalesController.java`
Manages sales and analytics data:
- View sales statistics
- Get product performance metrics
- Analyze sales by category and time period

### `SubscriptionController.java`
Manages user subscriptions:
- Create new subscriptions
- View active subscriptions
- Cancel subscriptions
- Check subscription status

### `SubscriptionPlanController.java`
Manages subscription plans:
- Create new plans (Basic, Pro, Enterprise)
- List all available plans
- Update plan pricing
- Delete plans

### `SuggestionsController.java`
Provides AI-powered suggestions:
- Get personalized product recommendations
- Business insights based on sales data

### `TransactionController.java`
Handles transaction records:
- View transaction history
- Get transaction details
- Filter transactions by status (success/failed)

### `UserController.java`
Manages user accounts:
- Create new users
- Get user information
- Update user details
- View user's subscription and payment data
