# Services

Services contain business logic - the "brains" of the application. They process data, apply rules, and coordinate between controllers and repositories.

## 📄 Files

### `SubscriptionService.java`
Handles subscription business logic:
- Creates new subscriptions for users
- Calculates subscription end dates based on billing cycle
- Determines next billing date
- Manages subscription cancellations
- Validates subscription rules (e.g., one active subscription per user)

### `SubscriptionPlanService.java`
Manages subscription plans:
- Validates plan data before saving
- Ensures plan pricing is reasonable
- Handles plan activation/deactivation
- Calculates plan features and benefits

### `BillService.java`
Handles billing operations:
- Generates bills automatically for active subscriptions
- Calculates bill amounts (monthly vs yearly pricing)
- Sets due dates (typically 7 days after generation)
- Updates bill status (PENDING → PAID → OVERDUE)
- Handles proration for mid-cycle changes

### `PaymentService.java`
Processes payments:
- Simulates payment gateway interaction (80% success, 20% fail)
- Creates transaction records in MongoDB
- Updates bill status after payment
- Handles failed payment retry logic
- Schedules automatic retries for failed payments

### `SalesService.java`
Manages sales analytics:
- Aggregates sales data by time period (daily, weekly, monthly)
- Calculates product performance metrics
- Generates category-wise sales reports
- Provides insights for business intelligence

### `SuggestionService.java`
Generates AI-powered recommendations:
- Analyzes user purchase history
- Provides personalized product suggestions
- Generates business insights based on sales trends
- Uses simple algorithms for recommendations (can be enhanced with ML)
