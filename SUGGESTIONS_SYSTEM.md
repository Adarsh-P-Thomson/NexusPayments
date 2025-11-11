# Business Suggestions System - Documentation

## Overview
An AI-powered business intelligence system that analyzes your sales data and provides actionable recommendations across 5 key categories.

## What It Analyzes

### Data Sources
- **500 sales transactions** across 84 products over 180 days
- **Customer behavior**: Premium vs regular customer patterns
- **Product performance**: Sales velocity, revenue, turnover
- **Regional trends**: Performance across 8 regions
- **Pricing patterns**: Discount usage, price elasticity
- **Inventory levels**: Current stock vs demand

## Suggestion Categories

### 1. INVENTORY Suggestions
**What it detects:**
- Top-performing products at risk of stockout
- Slow-moving inventory tying up capital
- Optimal restock quantities based on sales velocity

**Example suggestions:**
- "Restock High-Demand Product: Wireless Mouse - Stock depletes in 8 days, order 180 units"
- "Reduce Slow-Moving Inventory: Yoga Mat - 50 units in stock, sells 0.3/day, run clearance"

### 2. PRICING Suggestions
**What it detects:**
- Excessive discounting patterns
- Premium pricing opportunities for high-demand items
- Category-level pricing optimization

**Example suggestions:**
- "Optimize Discounting Strategy: Electronics - 8.2% discount rate, test value-based pricing"
- "Test Premium Pricing: Smart Watch - $199 avg order, strong demand, test 5% increase"

### 3. MARKETING Suggestions
**What it detects:**
- Premium membership conversion opportunities
- Cross-sell potential (customers buying from single category)
- Lapsed customer win-back opportunities
- Customer lifetime value optimization

**Example suggestions:**
- "Increase Premium Membership Conversion - Only 35.6% are premium, launch 3-month trial"
- "Cross-Category Promotion - 42% buy from one category, 'Complete Your Collection' campaign"
- "Win-Back Campaign for Lapsed Customers - 15 customers inactive 30-60 days"

### 4. REGIONAL Suggestions
**What it detects:**
- Underperforming regions vs average
- Top-performing regions with expansion potential
- Regional inventory allocation opportunities

**Example suggestions:**
- "Boost Sales in Latin America - $12,450 revenue (45% below average), increase marketing 30%"
- "Expand Success in North America - Top region $45,000, test localized product variations"

### 5. PRODUCT Bundling Suggestions
**What it detects:**
- Products frequently purchased together
- Cross-product affinity patterns
- Bundle opportunities to increase AOV

**Example suggestions:**
- "Create Product Bundle: Mechanical Keyboard + Gaming Mouse - Purchased together by 12 customers"

## How Suggestions Are Prioritized

### Priority Levels
- **HIGH**: Urgent issues (stockouts, lapsed customers, major revenue opportunities)
- **MEDIUM**: Optimization opportunities (pricing tests, bundling, regional campaigns)
- **LOW**: Long-term strategies (market expansion, gradual improvements)

### Impact Score (0-100)
Calculated based on:
- **Potential revenue impact**: How much money is at stake
- **Confidence level**: Strength of the data signal
- **Implementation effort**: How quickly you can act
- **Time sensitivity**: Urgency of the opportunity

## API Endpoints

```http
GET /api/suggestions                    # All suggestions (filterable by category, priority)
GET /api/suggestions/inventory          # Inventory optimization only
GET /api/suggestions/pricing            # Pricing strategies only
GET /api/suggestions/marketing          # Marketing campaigns only
GET /api/suggestions/regional           # Regional opportunities only
GET /api/suggestions/bundles            # Product bundling only
GET /api/suggestions/high-priority      # HIGH priority only
```

### Query Parameters
- `category`: INVENTORY | PRICING | MARKETING | REGIONAL | PRODUCT
- `priority`: HIGH | MEDIUM | LOW

## Frontend Features

### Dashboard View
- **Summary Cards**: Total suggestions, high-priority count, avg impact score
- **Category Filters**: Quick filter by suggestion type
- **Priority Filters**: Focus on high/medium/low priority items

### Each Suggestion Card Shows:
- **Title**: Clear, actionable description
- **Priority Badge**: Visual urgency indicator
- **Description**: Context and data backing the suggestion
- **Recommended Action**: Specific next step to take
- **Impact Metrics**: Current vs potential values
- **Impact Score Bar**: Visual representation of business impact
- **Action Buttons**: Implement, Learn More, Dismiss

## Business Metrics Tracked

### Inventory Metrics
- Sales velocity (units/day)
- Days until stockout
- Excess stock value
- Turnover rate

### Pricing Metrics
- Average discount rate
- Price elasticity signals
- Revenue per transaction
- Margin optimization potential

### Marketing Metrics
- Premium conversion rate (currently 35.6%)
- Cross-category penetration
- Customer reactivation rate
- Lifetime value indicators

### Regional Metrics
- Revenue per region
- Sales count per region
- Performance vs average
- Growth potential score

## Sample Suggestions Generated

Based on your current 500 sales:

### High Priority (Expected 3-5)
1. Restock alerts for top 5 products
2. Win-back campaign for 10-20 lapsed customers
3. Premium membership conversion (targeting 64.4% regular customers)

### Medium Priority (Expected 5-8)
1. Slow-moving inventory clearance (3-5 products)
2. Cross-category promotions
3. Regional performance boosting
4. Discount optimization for 2-3 categories
5. Product bundling (2-3 bundles)

### Low Priority (Expected 2-4)
1. Premium pricing tests for top products
2. Regional expansion in best markets
3. Long-term pricing experiments

## How to Use

### Step 1: Review High-Priority Items
Start with HIGH priority suggestions - these have immediate business impact.

### Step 2: Filter by Category
Focus on one area at a time (e.g., fix inventory issues first, then tackle pricing).

### Step 3: Implement Actions
Each suggestion has a specific "Recommended Action" - follow it precisely.

### Step 4: Track Results
Compare current vs potential values to measure success.

### Step 5: Refresh Regularly
Run suggestions weekly as sales data updates.

## Technical Details

### Analysis Engine
- **Language**: Java (Spring Boot)
- **Database**: PostgreSQL (inventory) + MongoDB (sales)
- **Real-time**: Computed on-demand from live data
- **Performance**: <1 second for 500 sales, <3 seconds for 5000 sales

### Algorithms Used
- **Velocity Scoring**: Linear regression on sales frequency
- **Customer Segmentation**: RFM-like clustering (Recency, Frequency, Monetary)
- **Affinity Analysis**: Co-purchase pattern detection
- **Threshold Rules**: Business logic for stockout/overstock alerts

## Future Enhancements (Not Yet Implemented)

- **Seasonal forecasting**: Predict demand spikes
- **Price elasticity modeling**: Quantify price sensitivity
- **A/B test suggestions**: Automated experiment design
- **Automated campaigns**: One-click email/SMS triggers
- **ML predictions**: Prophet/ARIMA forecasting integration
- **Anomaly detection**: Alert on unusual sales patterns

## Access

- **Frontend**: Navigate to `/suggestions` in the app
- **Backend API**: `http://localhost:8080/api/suggestions`
- **Refresh**: Data updates in real-time as new sales are recorded
