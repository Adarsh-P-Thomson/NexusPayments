import { useState, useEffect } from 'react';
import { BarChart, Bar, LineChart, Line, PieChart, Pie, Cell, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { transactionAPI, billAPI, subscriptionAPI, salesAPI } from '../services/api';
import { TrendingUp, TrendingDown, DollarSign, ShoppingCart, Users, Package, AlertCircle, CheckCircle } from 'lucide-react';

function Dashboard({ userId }) {
  const [transactionData, setTransactionData] = useState([]);
  const [billStats, setBillStats] = useState({});
  const [subscriptions, setSubscriptions] = useState([]);
  const [salesAnalytics, setSalesAnalytics] = useState(null);
  const [topProducts, setTopProducts] = useState([]);
  const [recentTrends, setRecentTrends] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [userId]);

  const loadDashboardData = async () => {
    try {
      const [
        transactionsResponse, 
        billsResponse, 
        subscriptionsResponse,
        analyticsResponse,
        topProductsResponse,
        dailyTrendsResponse
      ] = await Promise.all([
        transactionAPI.getUserTransactions(userId),
        billAPI.getUserBills(userId),
        subscriptionAPI.getUserSubscriptions(userId),
        salesAPI.getAnalytics({}),
        salesAPI.getTopProducts({ limit: 5 }),
        salesAPI.getSalesByPeriod({ period: 'daily' })
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
      setSalesAnalytics(analyticsResponse.data);
      setTopProducts(topProductsResponse.data);
      
      // Get last 7 days for trend
      const last7Days = dailyTrendsResponse.data.slice(-7);
      setRecentTrends(last7Days);
    } catch (error) {
      console.error('Error loading dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8'];

  // Calculate trend percentage
  const calculateTrend = (current, previous) => {
    if (!previous || previous === 0) return 0;
    return (((current - previous) / previous) * 100).toFixed(1);
  };

  // Get 7-day trend
  const getWeeklyTrend = () => {
    if (recentTrends.length < 2) return 0;
    const lastWeek = recentTrends.slice(0, 3).reduce((sum, day) => sum + day.revenue, 0);
    const thisWeek = recentTrends.slice(-3).reduce((sum, day) => sum + day.revenue, 0);
    return calculateTrend(thisWeek, lastWeek);
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto p-6 bg-gray-50 min-h-screen">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-4xl font-bold text-gray-800">Business Dashboard</h1>
        <p className="text-gray-600 mt-2">Welcome back! Here's what's happening with your business today.</p>
      </div>

      {/* Key Metrics Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        {/* Total Revenue */}
        <div className="bg-gradient-to-br from-blue-500 to-blue-600 text-white rounded-xl p-6 shadow-lg hover:shadow-xl transition-shadow">
          <div className="flex items-center justify-between mb-4">
            <div className="bg-white bg-opacity-30 p-3 rounded-lg">
              <DollarSign className="w-6 h-6" />
            </div>
            {getWeeklyTrend() > 0 ? (
              <TrendingUp className="w-5 h-5 text-green-200" />
            ) : (
              <TrendingDown className="w-5 h-5 text-red-200" />
            )}
          </div>
          <h3 className="text-sm font-medium opacity-90">Total Revenue</h3>
          <p className="text-3xl font-bold mt-1">
            ${salesAnalytics?.totalRevenue?.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) || '0.00'}
          </p>
          <p className="text-sm opacity-80 mt-2">
            <span className={getWeeklyTrend() > 0 ? 'text-green-200' : 'text-red-200'}>
              {getWeeklyTrend() > 0 ? '+' : ''}{getWeeklyTrend()}%
            </span> vs last period
          </p>
        </div>

        {/* Total Sales */}
        <div className="bg-gradient-to-br from-green-500 to-green-600 text-white rounded-xl p-6 shadow-lg hover:shadow-xl transition-shadow">
          <div className="flex items-center justify-between mb-4">
            <div className="bg-white bg-opacity-30 p-3 rounded-lg">
              <ShoppingCart className="w-6 h-6" />
            </div>
            <CheckCircle className="w-5 h-5 text-green-200" />
          </div>
          <h3 className="text-sm font-medium opacity-90">Total Orders</h3>
          <p className="text-3xl font-bold mt-1">{salesAnalytics?.totalSales?.toLocaleString() || 0}</p>
          <p className="text-sm opacity-80 mt-2">
            {salesAnalytics?.totalQuantitySold?.toLocaleString() || 0} items sold
          </p>
        </div>

        {/* Average Order Value */}
        <div className="bg-gradient-to-br from-purple-500 to-purple-600 text-white rounded-xl p-6 shadow-lg hover:shadow-xl transition-shadow">
          <div className="flex items-center justify-between mb-4">
            <div className="bg-white bg-opacity-30 p-3 rounded-lg">
              <Package className="w-6 h-6" />
            </div>
          </div>
          <h3 className="text-sm font-medium opacity-90">Average Order Value</h3>
          <p className="text-3xl font-bold mt-1">
            ${salesAnalytics?.averageOrderValue?.toFixed(2) || '0.00'}
          </p>
          <p className="text-sm opacity-80 mt-2">
            Per transaction
          </p>
        </div>

        {/* Customer Distribution */}
        <div className="bg-gradient-to-br from-orange-500 to-orange-600 text-white rounded-xl p-6 shadow-lg hover:shadow-xl transition-shadow">
          <div className="flex items-center justify-between mb-4">
            <div className="bg-white bg-opacity-30 p-3 rounded-lg">
              <Users className="w-6 h-6" />
            </div>
          </div>
          <h3 className="text-sm font-medium opacity-90">Premium Customers</h3>
          <p className="text-3xl font-bold mt-1">
            {salesAnalytics?.premiumCustomerSales || 0}
          </p>
          <p className="text-sm opacity-80 mt-2">
            {salesAnalytics?.totalSales 
              ? ((salesAnalytics.premiumCustomerSales / salesAnalytics.totalSales) * 100).toFixed(1) 
              : 0}% of total sales
          </p>
        </div>
      </div>

      {/* Secondary Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="bg-white rounded-lg p-5 shadow-md border border-gray-200">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Active Subscriptions</p>
              <p className="text-2xl font-bold text-gray-800 mt-1">
                {subscriptions.filter(s => s.status === 'ACTIVE').length}
              </p>
            </div>
            <div className="bg-blue-100 p-3 rounded-full">
              <CheckCircle className="w-6 h-6 text-blue-600" />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg p-5 shadow-md border border-gray-200">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Pending Bills</p>
              <p className="text-2xl font-bold text-gray-800 mt-1">{billStats.pending || 0}</p>
            </div>
            <div className="bg-yellow-100 p-3 rounded-full">
              <AlertCircle className="w-6 h-6 text-yellow-600" />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg p-5 shadow-md border border-gray-200">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Total Discounts Given</p>
              <p className="text-2xl font-bold text-gray-800 mt-1">
                ${salesAnalytics?.totalDiscounts?.toFixed(2) || '0.00'}
              </p>
            </div>
            <div className="bg-red-100 p-3 rounded-full">
              <DollarSign className="w-6 h-6 text-red-600" />
            </div>
          </div>
        </div>
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
        {/* Sales Trend (Last 7 Days) */}
        <div className="bg-white p-6 rounded-xl shadow-md border border-gray-200">
          <h2 className="text-xl font-semibold mb-4 text-gray-800">Sales Trend (Last 7 Days)</h2>
          {recentTrends?.length > 0 ? (
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={recentTrends}>
                <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
                <XAxis 
                  dataKey="period" 
                  stroke="#6b7280"
                  tick={{ fontSize: 12 }}
                />
                <YAxis stroke="#6b7280" tick={{ fontSize: 12 }} />
                <Tooltip 
                  contentStyle={{ 
                    backgroundColor: '#fff', 
                    border: '1px solid #e5e7eb',
                    borderRadius: '8px'
                  }}
                />
                <Legend />
                <Line 
                  type="monotone" 
                  dataKey="revenue" 
                  stroke="#3b82f6" 
                  strokeWidth={3}
                  name="Revenue ($)" 
                  dot={{ r: 5 }}
                  activeDot={{ r: 7 }}
                />
                <Line 
                  type="monotone" 
                  dataKey="salesCount" 
                  stroke="#10b981" 
                  strokeWidth={2}
                  name="Orders" 
                  dot={{ r: 4 }}
                />
              </LineChart>
            </ResponsiveContainer>
          ) : (
            <p className="text-gray-500 text-center py-20">No trend data available</p>
          )}
        </div>

        {/* Top Products */}
        <div className="bg-white p-6 rounded-xl shadow-md border border-gray-200">
          <h2 className="text-xl font-semibold mb-4 text-gray-800">Top 5 Products</h2>
          {topProducts?.length > 0 ? (
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={topProducts} layout="vertical">
                <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
                <XAxis type="number" stroke="#6b7280" tick={{ fontSize: 12 }} />
                <YAxis 
                  type="category" 
                  dataKey="productName" 
                  width={120}
                  stroke="#6b7280"
                  tick={{ fontSize: 11 }}
                />
                <Tooltip 
                  contentStyle={{ 
                    backgroundColor: '#fff', 
                    border: '1px solid #e5e7eb',
                    borderRadius: '8px'
                  }}
                />
                <Legend />
                <Bar 
                  dataKey="totalRevenue" 
                  fill="#8b5cf6" 
                  name="Revenue ($)"
                  radius={[0, 8, 8, 0]}
                />
              </BarChart>
            </ResponsiveContainer>
          ) : (
            <p className="text-gray-500 text-center py-20">No product data available</p>
          )}
        </div>

        {/* Transaction Status Distribution */}
        <div className="bg-white p-6 rounded-xl shadow-md border border-gray-200">
          <h2 className="text-xl font-semibold mb-4 text-gray-800">Transaction Status</h2>
          {transactionData.statusData?.length > 0 ? (
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={transactionData.statusData}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                  outerRadius={100}
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
            <p className="text-gray-500 text-center py-20">No transaction data available</p>
          )}
        </div>

        {/* Monthly Spending */}
        <div className="bg-white p-6 rounded-xl shadow-md border border-gray-200">
          <h2 className="text-xl font-semibold mb-4 text-gray-800">Monthly Spending</h2>
          {transactionData.spendingData?.length > 0 ? (
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={transactionData.spendingData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
                <XAxis 
                  dataKey="month" 
                  stroke="#6b7280"
                  tick={{ fontSize: 12 }}
                />
                <YAxis stroke="#6b7280" tick={{ fontSize: 12 }} />
                <Tooltip 
                  contentStyle={{ 
                    backgroundColor: '#fff', 
                    border: '1px solid #e5e7eb',
                    borderRadius: '8px'
                  }}
                />
                <Legend />
                <Bar 
                  dataKey="amount" 
                  fill="#f59e0b" 
                  name="Amount ($)"
                  radius={[8, 8, 0, 0]}
                />
              </BarChart>
            </ResponsiveContainer>
          ) : (
            <p className="text-gray-500 text-center py-20">No spending data available</p>
          )}
        </div>
      </div>

      {/* Bill Status Overview */}
      <div className="bg-white p-6 rounded-xl shadow-md border border-gray-200">
        <h2 className="text-xl font-semibold mb-6 text-gray-800">Bill Status Overview</h2>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
          <div className="border-l-4 border-blue-500 bg-blue-50 rounded-lg p-5">
            <p className="text-sm text-gray-600 font-medium">Total Bills</p>
            <p className="text-3xl font-bold text-gray-800 mt-2">{billStats.total || 0}</p>
          </div>
          <div className="border-l-4 border-green-500 bg-green-50 rounded-lg p-5">
            <p className="text-sm text-gray-600 font-medium">Paid Bills</p>
            <p className="text-3xl font-bold text-green-700 mt-2">{billStats.paid || 0}</p>
            <p className="text-xs text-gray-500 mt-1">
              ${billStats.paidAmount?.toFixed(2) || '0.00'}
            </p>
          </div>
          <div className="border-l-4 border-yellow-500 bg-yellow-50 rounded-lg p-5">
            <p className="text-sm text-gray-600 font-medium">Pending</p>
            <p className="text-3xl font-bold text-yellow-700 mt-2">{billStats.pending || 0}</p>
          </div>
          <div className="border-l-4 border-purple-500 bg-purple-50 rounded-lg p-5">
            <p className="text-sm text-gray-600 font-medium">Success Rate</p>
            <p className="text-3xl font-bold text-purple-700 mt-2">
              {billStats.total > 0 ? ((billStats.paid / billStats.total) * 100).toFixed(1) : 0}%
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
