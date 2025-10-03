import { useState, useEffect } from 'react';
import { LineChart, Line, BarChart, Bar, PieChart, Pie, Cell, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { transactionAPI, billAPI, subscriptionAPI } from '../services/api';

function Dashboard({ userId }) {
  const [transactionData, setTransactionData] = useState([]);
  const [billStats, setBillStats] = useState({});
  const [subscriptions, setSubscriptions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  }, [userId]);

  const loadDashboardData = async () => {
    try {
      const [transactionsResponse, billsResponse, subscriptionsResponse] = await Promise.all([
        transactionAPI.getUserTransactions(userId),
        billAPI.getUserBills(userId),
        subscriptionAPI.getUserSubscriptions(userId),
      ]);

      // Process transaction data for charts
      const transactions = transactionsResponse.data;
      const bills = billsResponse.data;
      
      // Transaction status distribution
      const statusCount = {};
      transactions.forEach(t => {
        statusCount[t.status] = (statusCount[t.status] || 0) + 1;
      });

      const statusData = Object.entries(statusCount).map(([name, value]) => ({
        name,
        value,
      }));

      // Monthly spending data
      const monthlySpending = {};
      transactions.filter(t => t.status === 'SUCCESS').forEach(t => {
        const month = new Date(t.transactionDate).toLocaleDateString('en-US', { month: 'short', year: 'numeric' });
        monthlySpending[month] = (monthlySpending[month] || 0) + parseFloat(t.amount);
      });

      const spendingData = Object.entries(monthlySpending).map(([month, amount]) => ({
        month,
        amount,
      }));

      // Bill statistics
      const billStatistics = {
        total: bills.length,
        paid: bills.filter(b => b.status === 'PAID').length,
        pending: bills.filter(b => b.status === 'PENDING').length,
        failed: bills.filter(b => b.status === 'FAILED').length,
        totalAmount: bills.reduce((sum, b) => sum + parseFloat(b.amount), 0),
        paidAmount: bills.filter(b => b.status === 'PAID').reduce((sum, b) => sum + parseFloat(b.amount), 0),
      };

      setTransactionData({
        statusData,
        spendingData,
      });
      setBillStats(billStatistics);
      setSubscriptions(subscriptionsResponse.data);
    } catch (error) {
      console.error('Error loading dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8'];

  if (loading) {
    return <div className="flex justify-center p-8">Loading dashboard...</div>;
  }

  return (
    <div className="max-w-7xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">Dashboard</h1>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
        <div className="bg-blue-500 text-white rounded-lg p-4">
          <h3 className="text-lg font-semibold">Active Subscriptions</h3>
          <p className="text-3xl font-bold">{subscriptions.filter(s => s.status === 'ACTIVE').length}</p>
        </div>
        <div className="bg-green-500 text-white rounded-lg p-4">
          <h3 className="text-lg font-semibold">Paid Bills</h3>
          <p className="text-3xl font-bold">{billStats.paid}</p>
        </div>
        <div className="bg-yellow-500 text-white rounded-lg p-4">
          <h3 className="text-lg font-semibold">Pending Bills</h3>
          <p className="text-3xl font-bold">{billStats.pending}</p>
        </div>
        <div className="bg-purple-500 text-white rounded-lg p-4">
          <h3 className="text-lg font-semibold">Total Spent</h3>
          <p className="text-3xl font-bold">${billStats.paidAmount?.toFixed(2) || '0.00'}</p>
        </div>
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
        {/* Transaction Status Distribution */}
        <div className="bg-white p-4 rounded-lg shadow">
          <h2 className="text-xl font-semibold mb-4">Transaction Status Distribution</h2>
          {transactionData.statusData?.length > 0 ? (
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={transactionData.statusData}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {transactionData.statusData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          ) : (
            <p className="text-gray-500 text-center">No transaction data available</p>
          )}
        </div>

        {/* Monthly Spending */}
        <div className="bg-white p-4 rounded-lg shadow">
          <h2 className="text-xl font-semibold mb-4">Monthly Spending</h2>
          {transactionData.spendingData?.length > 0 ? (
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={transactionData.spendingData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="amount" fill="#8884d8" name="Amount ($)" />
              </BarChart>
            </ResponsiveContainer>
          ) : (
            <p className="text-gray-500 text-center">No spending data available</p>
          )}
        </div>
      </div>

      {/* Bill Status Overview */}
      <div className="bg-white p-4 rounded-lg shadow">
        <h2 className="text-xl font-semibold mb-4">Bill Status Overview</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="border rounded-lg p-4">
            <p className="text-gray-600">Total Bills</p>
            <p className="text-2xl font-bold">{billStats.total}</p>
          </div>
          <div className="border rounded-lg p-4">
            <p className="text-gray-600">Total Amount</p>
            <p className="text-2xl font-bold">${billStats.totalAmount?.toFixed(2) || '0.00'}</p>
          </div>
          <div className="border rounded-lg p-4">
            <p className="text-gray-600">Payment Success Rate</p>
            <p className="text-2xl font-bold">
              {billStats.total > 0 ? ((billStats.paid / billStats.total) * 100).toFixed(1) : 0}%
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
