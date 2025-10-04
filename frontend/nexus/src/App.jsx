import { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import Dashboard from './components/Dashboard';
import SubscriptionManagement from './components/SubscriptionManagement';
import PaymentManagement from './components/PaymentManagement';
import SubscriptionPlanManager from './components/SubscriptionPlanManager';
import DataInitializer from './components/DataInitializer';
import './App.css';

function App() {
  // For demo purposes, using a fixed user ID. In production, this would come from authentication
  const [userId] = useState(1);

  return (
    <Router>
      <div className="min-h-screen bg-gray-50">
        {/* Navigation */}
        <nav className="bg-blue-600 text-white shadow-lg">
          <div className="max-w-7xl mx-auto px-4">
            <div className="flex items-center justify-between h-16">
              <div className="flex items-center">
                <h1 className="text-2xl font-bold">NexusPay</h1>
              </div>
              <div className="flex space-x-4">
                <Link to="/" className="px-3 py-2 rounded-md hover:bg-blue-700">
                  Dashboard
                </Link>
                <Link to="/subscriptions" className="px-3 py-2 rounded-md hover:bg-blue-700">
                  Subscriptions
                </Link>
                <Link to="/payments" className="px-3 py-2 rounded-md hover:bg-blue-700">
                  Payments
                </Link>
                <Link to="/plan-manager" className="px-3 py-2 rounded-md hover:bg-blue-700">
                  Plan Manager
                </Link>
                <Link to="/initialize" className="px-3 py-2 rounded-md hover:bg-blue-700">
                  Initialize Data
                </Link>
              </div>
            </div>
          </div>
        </nav>

        {/* Main Content */}
        <main className="py-6">
          <Routes>
            <Route path="/" element={<Dashboard userId={userId} />} />
            <Route path="/subscriptions" element={<SubscriptionManagement userId={userId} />} />
            <Route path="/payments" element={<PaymentManagement userId={userId} />} />
            <Route path="/plan-manager" element={<SubscriptionPlanManager />} />
            <Route path="/initialize" element={<DataInitializer />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;
