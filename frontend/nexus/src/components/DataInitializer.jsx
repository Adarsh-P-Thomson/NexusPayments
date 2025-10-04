import { useState } from 'react';
import { userAPI, subscriptionPlanAPI } from '../services/api';

function DataInitializer() {
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  const initializeData = async () => {
    setLoading(true);
    setMessage('Initializing data...');

    try {
      // Create a test user
      const userResponse = await userAPI.createUser({
        email: 'demo@nexuspay.com',
        name: 'Demo User',
      });
      
      const userId = userResponse.data.id;
      setMessage(`Created user with ID: ${userId}`);

      // Create sample subscription plans
      const plans = [
        {
          name: 'Basic Plan',
          description: 'Perfect for individuals and small teams',
          monthlyPrice: 9.99,
          yearlyPrice: 99.99,
          features: 'Up to 5 users, 10GB storage, Email support',
          active: true,
        },
        {
          name: 'Professional Plan',
          description: 'Ideal for growing businesses',
          monthlyPrice: 29.99,
          yearlyPrice: 299.99,
          features: 'Up to 50 users, 100GB storage, Priority support, API access',
          active: true,
        },
        {
          name: 'Enterprise Plan',
          description: 'For large organizations',
          monthlyPrice: 99.99,
          yearlyPrice: 999.99,
          features: 'Unlimited users, 1TB storage, 24/7 support, Custom integrations',
          active: true,
        },
      ];

      for (const plan of plans) {
        await subscriptionPlanAPI.createPlan(plan);
      }

      setMessage(`✓ Successfully initialized data! Created user (ID: ${userId}) and ${plans.length} subscription plans.`);
    } catch (error) {
      console.error('Error initializing data:', error);
      setMessage(`Error: ${error.response?.data?.message || error.message || 'Failed to initialize data'}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto p-6">
      <div className="bg-white rounded-lg shadow p-6">
        <h2 className="text-2xl font-bold mb-4">Data Initializer</h2>
        <p className="text-gray-600 mb-4">
          Click the button below to initialize the database with sample data (demo user and subscription plans).
        </p>
        
        <button
          onClick={initializeData}
          disabled={loading}
          className="bg-blue-500 text-white px-6 py-2 rounded hover:bg-blue-600 disabled:bg-gray-400"
        >
          {loading ? 'Initializing...' : 'Initialize Data'}
        </button>

        {message && (
          <div className={`mt-4 p-4 rounded ${message.startsWith('Error') || message.startsWith('✗') ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'}`}>
            {message}
          </div>
        )}

        <div className="mt-6 p-4 bg-gray-100 rounded">
          <h3 className="font-semibold mb-2">What will be created:</h3>
          <ul className="list-disc list-inside space-y-1 text-sm text-gray-700">
            <li>Demo user (demo@nexuspay.com)</li>
            <li>Basic Plan ($9.99/month or $99.99/year)</li>
            <li>Professional Plan ($29.99/month or $299.99/year)</li>
            <li>Enterprise Plan ($99.99/month or $999.99/year)</li>
          </ul>
        </div>
      </div>
    </div>
  );
}

export default DataInitializer;
