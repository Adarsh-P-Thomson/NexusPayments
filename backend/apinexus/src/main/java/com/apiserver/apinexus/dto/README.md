# DTOs (Data Transfer Objects)

DTOs are simple objects used to transfer data between the frontend and backend. They define what data comes in requests and what goes out in responses.

## 📄 Files

### `SubscriptionRequest.java`
**Purpose**: Request to create a new subscription  
**Contains**:
- User ID (who is subscribing)
- Plan ID (which plan they want)
- Billing cycle (MONTHLY or YEARLY)

### `SubscriptionPlanDTO.java`
**Purpose**: Simplified subscription plan data for frontend  
**Contains**:
- Plan name
- Monthly price
- Yearly price
- Features list
- Whether it's active

### `PaymentRequest.java`
**Purpose**: Request to process a payment  
**Contains**:
- Bill ID (which bill to pay)
- User ID (who is paying)
- Payment method details
- Card information

### `PaymentResponse.java`
**Purpose**: Response after payment attempt  
**Contains**:
- Success/failure status
- Transaction ID
- Error message (if failed)
- Next retry date (if failed)

### `SalesAnalyticsDTO.java`
**Purpose**: Sales analytics summary data  
**Contains**:
- Total sales amount
- Number of transactions
- Time period covered
- Growth percentage

### `CategorySalesDTO.java`
**Purpose**: Sales data grouped by product category  
**Contains**:
- Category name
- Total sales for that category
- Number of items sold
- Percentage of total sales

### `ProductSalesDTO.java`
**Purpose**: Individual product sales performance  
**Contains**:
- Product name
- Quantity sold
- Revenue generated
- Average price

### `TimePeriodSalesDTO.java`
**Purpose**: Sales data for specific time periods  
**Contains**:
- Period (day, week, month)
- Sales amount
- Number of transactions
- Comparison to previous period

### `ProductPerformanceDTO.java`
**Purpose**: Detailed product performance metrics  
**Contains**:
- Product ID and name
- Total revenue
- Units sold
- Growth rate
- Ranking

### `SuggestionDTO.java`
**Purpose**: AI-generated recommendation  
**Contains**:
- Suggestion text
- Confidence score
- Category
- Reasoning
