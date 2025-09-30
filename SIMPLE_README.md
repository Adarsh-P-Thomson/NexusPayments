# NexusPay Simple Demo

A simplified, fully functional demo version of the NexusPay billing platform built with React, TypeScript, and JSON data.

## 🚀 Quick Start

```bash
cd frontend
npm install
npm run dev
```

Open [http://localhost:3000](http://localhost:3000) to view the demo.

## 📸 Screenshots

![NexusPay Dashboard](https://github.com/user-attachments/assets/3e7ab947-3206-4097-bf29-313474650b6e)

## ✨ Features

### Dashboard
- Revenue overview cards showing total revenue, active tenants, and key metrics
- Interactive charts for monthly revenue and tenant growth
- Usage statistics (API calls, storage, bandwidth)
- Recent activity feeds

### Subscriptions Management
- Card-based subscription display with plan details
- Real-time usage metrics for each subscription
- Feature lists and status indicators
- Summary statistics

### Invoice Management
- Complete invoice listing with filters and search
- Detailed invoice view with line items
- Professional invoice layout
- Status tracking and payment information

## 🛠 Tech Stack

- **React 19** - Modern React with hooks
- **TypeScript** - Type safety and better development experience
- **Tailwind CSS** - Utility-first CSS framework
- **Recharts** - Interactive data visualization
- **Lucide React** - Beautiful icons
- **Vite** - Fast development and building

## 📊 Demo Data

The application uses realistic JSON data located in `src/data/mockData.json` containing:

- 3 example tenants (TechStart Inc, DataFlow Solutions, StartupLab)
- 3 subscription plans (Basic, Premium, Enterprise)
- 3 invoices with detailed line items
- Revenue and usage metrics
- Historical data for charts

## 🎯 Purpose

This demo showcases the core concept of NexusPay - a multi-tenant SaaS billing platform with:

- Subscription management
- Usage-based billing
- Invoice generation
- Analytics and reporting
- Professional dashboard interface

Perfect for presentations, proof-of-concept demonstrations, and stakeholder reviews.

## 🔧 Commands

```bash
# Development
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Lint code
npm run lint
```

## 📁 Project Structure

```
frontend/
├── src/
│   ├── components/         # React components
│   │   ├── Dashboard.tsx   # Main dashboard view
│   │   ├── Subscriptions.tsx # Subscription management
│   │   ├── Invoices.tsx    # Invoice management
│   │   └── Navigation.tsx  # Top navigation
│   ├── data/
│   │   └── mockData.json   # Demo data
│   ├── App.tsx            # Main app component
│   └── main.tsx           # App entry point
├── package.json
└── vite.config.ts
```