import React from 'react';
import { CreditCard, Calendar, Users, Check } from 'lucide-react';
import mockData from '../data/mockData.json';

const Subscriptions: React.FC = () => {
  const { subscriptions, tenants } = mockData;

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'active':
        return 'bg-green-100 text-green-800';
      case 'trial':
        return 'bg-blue-100 text-blue-800';
      case 'cancelled':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getTenantName = (tenantId: string) => {
    const tenant = tenants.find(t => t.id === tenantId);
    return tenant ? tenant.name : 'Unknown Tenant';
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold text-gray-900">Subscriptions</h2>
        <button className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium">
          Add Subscription
        </button>
      </div>

      {/* Subscription Cards */}
      <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6">
        {subscriptions.map((subscription) => (
          <div key={subscription.id} className="bg-white rounded-lg shadow-lg p-6 border border-gray-200">
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center">
                <div className="p-2 bg-blue-100 rounded-lg">
                  <CreditCard className="h-5 w-5 text-blue-600" />
                </div>
                <div className="ml-3">
                  <h3 className="text-lg font-semibold text-gray-900">{subscription.planName}</h3>
                  <p className="text-sm text-gray-600">{getTenantName(subscription.tenantId)}</p>
                </div>
              </div>
              <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(subscription.status)}`}>
                {subscription.status}
              </span>
            </div>

            <div className="space-y-3">
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Price</span>
                <span className="font-semibold text-gray-900">${subscription.price}/{subscription.interval}</span>
              </div>

              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">Current Period</span>
                <span className="text-sm text-gray-900">
                  {subscription.currentPeriodStart} - {subscription.currentPeriodEnd}
                </span>
              </div>

              <div className="border-t pt-3">
                <p className="text-sm font-medium text-gray-900 mb-2">Features</p>
                <ul className="space-y-1">
                  {subscription.features.map((feature, index) => (
                    <li key={index} className="flex items-center text-sm text-gray-600">
                      <Check className="h-4 w-4 text-green-500 mr-2" />
                      {feature}
                    </li>
                  ))}
                </ul>
              </div>

              <div className="border-t pt-3">
                <p className="text-sm font-medium text-gray-900 mb-2">Usage This Period</p>
                <div className="space-y-2">
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-600">API Calls</span>
                    <span className="text-gray-900">{subscription.usageMetrics.apiCalls.toLocaleString()}</span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-600">Storage</span>
                    <span className="text-gray-900">{subscription.usageMetrics.storage}</span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-600">Bandwidth</span>
                    <span className="text-gray-900">{subscription.usageMetrics.bandwidth}</span>
                  </div>
                </div>
              </div>
            </div>

            <div className="mt-4 pt-4 border-t flex space-x-2">
              <button className="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-800 py-2 px-3 rounded text-sm font-medium">
                View Details
              </button>
              <button className="flex-1 bg-blue-600 hover:bg-blue-700 text-white py-2 px-3 rounded text-sm font-medium">
                Manage
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Summary Stats */}
      <div className="bg-white rounded-lg shadow p-6">
        <h3 className="text-lg font-medium text-gray-900 mb-4">Subscription Summary</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="text-center">
            <div className="flex items-center justify-center mb-2">
              <Users className="h-8 w-8 text-blue-600" />
            </div>
            <p className="text-2xl font-semibold text-gray-900">{subscriptions.length}</p>
            <p className="text-sm text-gray-600">Total Subscriptions</p>
          </div>
          <div className="text-center">
            <div className="flex items-center justify-center mb-2">
              <Calendar className="h-8 w-8 text-green-600" />
            </div>
            <p className="text-2xl font-semibold text-gray-900">{subscriptions.filter(s => s.status === 'active').length}</p>
            <p className="text-sm text-gray-600">Active Subscriptions</p>
          </div>
          <div className="text-center">
            <div className="flex items-center justify-center mb-2">
              <CreditCard className="h-8 w-8 text-purple-600" />
            </div>
            <p className="text-2xl font-semibold text-gray-900">
              ${subscriptions.reduce((sum, sub) => sum + sub.price, 0).toFixed(0)}
            </p>
            <p className="text-sm text-gray-600">Monthly Revenue</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Subscriptions;