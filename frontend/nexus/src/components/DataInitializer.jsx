import { useState } from 'react';
import { initAPI } from '../services/api';

function DataInitializer() {
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  const initializeData = async () => {
    setLoading(true);
    setMessage('Initializing data...');

    try {
      // Use the backend initialization endpoint
      const response = await initAPI.initializeData();
      
      if (response.data.success) {
        const messages = response.data.messages.join(', ');
        setMessage(`✓ Success! ${messages}. 
                    Users: ${response.data.userCount}, 
                    Plans: ${response.data.planCount}, 
                    Cards: ${response.data.cardCount}, 
                    Transactions: ${response.data.transactionCount}`);
      } else {
        setMessage(`Error: ${response.data.error || 'Failed to initialize data'}`);
      }
    } catch (error) {
      console.error('Error initializing data:', error);
      setMessage(`Error: ${error.response?.data?.error || error.message || 'Failed to initialize data'}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto p-6">
      <div className="bg-white rounded-lg shadow p-6">
        <h2 className="text-2xl font-bold mb-4">Data Initializer</h2>
        <p className="text-gray-600 mb-4">
          Click the button below to initialize the database with sample data (demo users, subscription plans, and sample transactions).
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
            <li>John Doe (john.doe@example.com)</li>
            <li>Jane Smith (jane.smith@example.com)</li>
            <li>Basic Plan ($9.99/month or $99.99/year)</li>
            <li>Professional Plan ($29.99/month or $299.99/year)</li>
            <li>Enterprise Plan ($99.99/month or $999.99/year)</li>
            <li>Sample transaction data in MongoDB</li>
          </ul>
        </div>
      </div>
    </div>
  );
}

export default DataInitializer;
